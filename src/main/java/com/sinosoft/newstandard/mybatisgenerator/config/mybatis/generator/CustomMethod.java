package com.sinosoft.newstandard.mybatisgenerator.config.mybatis.generator;

import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.*;

import java.util.ListIterator;

/**
 * @Author: Eric
 **/
public class CustomMethod extends Method {

    private Boolean isAddEndingAnnotation;

    @Override
    public String getFormattedContent(int indentLevel, boolean interfaceMethod, CompilationUnit compilationUnit) {
        StringBuilder sb = new StringBuilder();

        addFormattedJavadoc(sb, indentLevel);
        addFormattedAnnotations(sb, indentLevel);

        OutputUtilities.javaIndent(sb, indentLevel);

        if (interfaceMethod) {
            if (isStatic()) {
                sb.append("static "); //$NON-NLS-1$
            } else if (isDefault()) {
                sb.append("default "); //$NON-NLS-1$
            }
        } else {
            sb.append(getVisibility().getValue());

            if (isStatic()) {
                sb.append("static "); //$NON-NLS-1$
            }

            if (isFinal()) {
                sb.append("final "); //$NON-NLS-1$
            }

            if (isSynchronized()) {
                sb.append("synchronized "); //$NON-NLS-1$
            }

            if (isNative()) {
                sb.append("native "); //$NON-NLS-1$
            } else if (super.getBodyLines().size() == 0) {
                sb.append("abstract "); //$NON-NLS-1$
            }
        }

        if (!getTypeParameters().isEmpty()) {
            sb.append("<"); //$NON-NLS-1$
            boolean comma = false;
            for (TypeParameter typeParameter : getTypeParameters()) {
                if (comma) {
                    sb.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }

                sb.append(typeParameter.getFormattedContent(compilationUnit));
            }
            sb.append("> "); //$NON-NLS-1$
        }

        if (!super.isConstructor()) {
            if (getReturnType() == null) {
                sb.append("void"); //$NON-NLS-1$
            } else {
                sb.append(JavaDomUtils.calculateTypeName(compilationUnit, getReturnType()));
            }
            sb.append(' ');
        }

        sb.append(getName());
        sb.append('(');

        boolean comma = false;
        for (Parameter parameter : getParameters()) {
            if (comma) {
                sb.append(", "); //$NON-NLS-1$
            } else {
                comma = true;
            }

            sb.append(parameter.getFormattedContent(compilationUnit));
        }

        sb.append(')');

        if (getExceptions().size() > 0) {
            sb.append(" throws "); //$NON-NLS-1$
            comma = false;
            for (FullyQualifiedJavaType fqjt : getExceptions()) {
                if (comma) {
                    sb.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }

                sb.append(JavaDomUtils.calculateTypeName(compilationUnit, fqjt));
            }
        }

        // if no body lines, then this is an abstract method
        if (super.getBodyLines().size() == 0 || isNative()) {
            sb.append(';');
        } else {
            sb.append(" {"); //$NON-NLS-1$
            indentLevel++;

            ListIterator<String> listIter = super.getBodyLines().listIterator();
            while (listIter.hasNext()) {
                String line = listIter.next();
                if (line.startsWith("}")) { //$NON-NLS-1$
                    indentLevel--;
                }

                OutputUtilities.newLine(sb);
                OutputUtilities.javaIndent(sb, indentLevel);
                sb.append(line);

                if ((line.endsWith("{") && !line.startsWith("switch")) //$NON-NLS-1$ //$NON-NLS-2$
                        || line.endsWith(":")) { //$NON-NLS-1$
                    indentLevel++;
                }

                if (line.startsWith("break")) { //$NON-NLS-1$
                    // if the next line is '}', then don't outdent
                    if (listIter.hasNext()) {
                        String nextLine = listIter.next();
                        if (nextLine.startsWith("}")) { //$NON-NLS-1$
                            indentLevel++;
                        }

                        // set back to the previous element
                        listIter.previous();
                    }
                    indentLevel--;
                }
            }

            indentLevel--;
            OutputUtilities.newLine(sb);
            OutputUtilities.javaIndent(sb, indentLevel);
            sb.append('}');
        }

        if (isAddEndingAnnotation) {
            sb.append(System.getProperty("line.separator"));
            sb.append(System.getProperty("line.separator"));
            sb.append("    //====================以下是自定义方法====================");
        }
        return sb.toString();
    }

    public Boolean getAddEndingAnnotation() {
        return isAddEndingAnnotation;
    }

    public void setAddEndingAnnotation(Boolean addEndingAnnotation) {
        isAddEndingAnnotation = addEndingAnnotation;
    }
}
