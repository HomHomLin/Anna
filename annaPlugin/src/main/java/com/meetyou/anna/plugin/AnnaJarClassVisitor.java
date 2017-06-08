package com.meetyou.anna.plugin;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;

/**
 * Created by Linhh on 17/6/8.
 */

public class AnnaJarClassVisitor extends ClassVisitor {
    private boolean mAnnaInject = true;
    private String mInjectClazz;
    private String mClazzName;

    public AnnaJarClassVisitor(String injectClazz, int api, ClassVisitor cv) {
        super(api, cv);
        mInjectClazz = injectClazz;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        mClazzName = name.replace("/",".");
//        AnnotationVisitor av0 = cv.visitAnnotation("Lcom/meetyou/anna/plugin/AntiAssassin;", true);
//        av0.visitEnd();

    }

    @Override
    public org.objectweb.asm.AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (Type.getDescriptor(AntiAssassin.class).equals(desc)) {
            mAnnaInject = false;
        }
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                     String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        methodVisitor = new AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, desc) {

            public void print(String msg){
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn(msg);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                        "(Ljava/lang/String;)V", false);
            }

            @Override
            protected void onMethodEnter() {
                if(!mAnnaInject){
                    return;
                }
                mv.visitMethodInsn(INVOKESTATIC, mInjectClazz, "getInject", "()L" + mInjectClazz + ";", false);
                mv.visitLdcInsn(mClazzName);
                if ((methodAccess & ACC_STATIC) == 0) {
                    loadThis();
                } else {
                    push((String) null);
                }
                mv.visitLdcInsn(name);
                loadArgArray();
                mv.visitLdcInsn(Type.getReturnType(methodDesc).toString());
                mv.visitMethodInsn(INVOKEVIRTUAL, mInjectClazz, "onMethodEnter", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/String;)V", false);
            }

            @Override
            protected void onMethodExit(int i) {
                if(!mAnnaInject){
                    return;
                }
                mv.visitMethodInsn(INVOKESTATIC, mInjectClazz, "getInject", "()L" + mInjectClazz + ";", false);
                mv.visitLdcInsn(mClazzName);
                if ((methodAccess & ACC_STATIC) == 0) {
                    loadThis();
                } else {
                    push((String) null);
                }
                mv.visitLdcInsn(name);
                loadArgArray();
                mv.visitLdcInsn(Type.getReturnType(methodDesc).toString());
                mv.visitMethodInsn(INVOKEVIRTUAL, mInjectClazz, "onMethodExit", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/String;)V", false);
            }
        };
        return methodVisitor;

    }
}
