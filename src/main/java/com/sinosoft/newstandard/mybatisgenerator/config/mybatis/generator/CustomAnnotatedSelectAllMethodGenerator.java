package com.sinosoft.newstandard.mybatisgenerator.config.mybatis.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

import java.util.Iterator;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;
import static org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.getSelectListPhrase;
import static org.mybatis.generator.internal.util.StringUtility.escapeStringForJava;

/**
 * @Author: Eric
 */
public class CustomAnnotatedSelectAllMethodGenerator extends CustomAbstractSelectAllMethodGenerator {

    private boolean useResultMapIfAvailable;

    public CustomAnnotatedSelectAllMethodGenerator(IntrospectedTable introspectedTable, boolean useResultMapIfAvailable, boolean isSimple) {
        super(isSimple);
        this.useResultMapIfAvailable = useResultMapIfAvailable;
        this.introspectedTable = introspectedTable;
    }

    @Override
    public void addMapperAnnotations(Interface interfaze, Method method) {

        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Select"));

        StringBuilder sb = new StringBuilder();

        method.addAnnotation("@Select({");

        javaIndent(sb, 1);
        sb.append("\"select\",");
        method.addAnnotation(sb.toString());


        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append('"');
        boolean hasColumns = false;
        Iterator<IntrospectedColumn> iter = introspectedTable.getAllColumns().iterator();
        while (iter.hasNext()) {
            sb.append(escapeStringForJava(getSelectListPhrase(iter.next())));
            hasColumns = true;
            if (iter.hasNext()) {
                sb.append(", ");
            }
            if (sb.length() > 80) {
                sb.append("\",");
                method.addAnnotation(sb.toString());
                sb.setLength(0);
                javaIndent(sb, 1);
                sb.append('"');
                hasColumns = false;
            }
        }

        if (hasColumns) {
            sb.append("\",");
            method.addAnnotation(sb.toString());
        }

        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append("\"from ");
        sb.append(escapeStringForJava(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));
        sb.append("\"");
        method.addAnnotation(sb.toString());

        method.addAnnotation("})");

        if (useResultMapIfAvailable) {
            if (introspectedTable.getRules().generateBaseResultMap() || introspectedTable.getRules().generateResultMapWithBLOBs()) {
                addResultMapAnnotation(interfaze, method);
            } else {
                addAnnotatedResults(interfaze, method);
            }
        } else {
            addAnnotatedResults(interfaze, method);
        }
    }

    private void addResultMapAnnotation(Interface interfaze, Method method) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.ResultMap"));

        String annotation = String.format("@ResultMap(\"%s\")", introspectedTable.getBaseRecordType().substring(introspectedTable.getBaseRecordType().lastIndexOf(".") + 1).toLowerCase());
        method.addAnnotation(annotation);
    }

    private void addAnnotatedResults(Interface interfaze, Method method) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType"));

        if (introspectedTable.isConstructorBased()) {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg"));
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs"));
            method.addAnnotation("@ConstructorArgs({");
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result"));
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results"));
            method.addAnnotation(String.format("@Results(id = \"%s\", value= {", introspectedTable.getBaseRecordType().substring(introspectedTable.getBaseRecordType().lastIndexOf(".") + 1).toLowerCase()));
        }
        StringBuilder sb = new StringBuilder();

        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
        while (iterPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(interfaze, introspectedColumn, true, introspectedTable.isConstructorBased()));

            if (iterPk.hasNext() || iterNonPk.hasNext()) {
                sb.append(',');
            }
            method.addAnnotation(sb.toString());
        }

        while (iterNonPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterNonPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(interfaze, introspectedColumn, false, introspectedTable.isConstructorBased()));

            if (iterNonPk.hasNext()) {
                sb.append(',');
            }
            method.addAnnotation(sb.toString());
        }
        method.addAnnotation("})");
    }
}