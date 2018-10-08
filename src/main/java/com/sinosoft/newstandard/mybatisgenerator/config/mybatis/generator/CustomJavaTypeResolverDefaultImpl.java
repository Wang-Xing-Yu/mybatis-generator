package com.sinosoft.newstandard.mybatisgenerator.config.mybatis.generator;


import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * @Author: Eric
 **/
public class CustomJavaTypeResolverDefaultImpl extends JavaTypeResolverDefaultImpl {

    public CustomJavaTypeResolverDefaultImpl() {
        super();
        // 把数据库的 NVARCHAR2 映射成 String
        super.typeMap.put(Types.OTHER, new JdbcTypeInformation("VARCHAR", new FullyQualifiedJavaType(String.class.getName())));
    }

    @Override
    protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer = defaultType;

        switch (column.getJdbcType()) {
            case Types.BIT:
                answer = calculateBitReplacement(column, defaultType);
                break;
            case Types.CHAR:
                answer = calculateCharReplacement(column, defaultType);
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                answer = calculateBigDecimalReplacement(column, defaultType);
                break;
            default:
                break;
        }

        return answer;
    }

    @Override
    protected FullyQualifiedJavaType calculateBigDecimalReplacement(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer;
        if (column.getScale() > 0 || column.getLength() > 19 || forceBigDecimals) {
            answer = defaultType;
        } else if (column.getLength() > 9) {
            answer = new FullyQualifiedJavaType(Long.class.getName());
        } else {
            answer = new FullyQualifiedJavaType(Integer.class.getName());
        }
        return answer;
    }

    /**
     * CHAR(1)生成Boolean类型
     */
    private FullyQualifiedJavaType calculateCharReplacement(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer;
        if (column.getScale() > 0 || column.getLength() > 1) {
            answer = defaultType;
        } else {
            answer = new FullyQualifiedJavaType(Boolean.class.getName());
        }
        return answer;
    }

}
