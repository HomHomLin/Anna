package com.meetyou.anna.plugin;

import com.meetyou.anna.inject.support.AnnaInjected;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linhh on 17/6/8.
 */

public class AnnaClassVisitor extends ClassVisitor {
    private boolean mAnnaInject = true;
    private String mInjectClazz;
    private String mClazzName;

    public AnnaClassVisitor(String injectClazz, int api, ClassVisitor cv) {
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
        if (Type.getDescriptor(AntiAssassin.class).equals(desc) || Type.getDescriptor(AnnaInjected.class).equals(desc)) {
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
//                if(AsmUtils.CLASS_INITIALIZER.equals(name)||AsmUtils.CONSTRUCTOR.equals(name)){
//                    return;
//                }
                //@warn 这部分代码请重点review一下，判断条件写错会要命
                //这部分代码请重点review一下，判断条件写错会要命
                // synthetic 方法暂时不aop 比如AsyncTask 会生成一些同名 synthetic方法,对synthetic 以及private的方法也插入的代码，主要是针对lambda表达式
                if(((access& Opcodes.ACC_SYNTHETIC) != 0)&&((access & Opcodes.ACC_PRIVATE)==0)){
                    return;
                }
                if ((access & Opcodes.ACC_NATIVE) != 0) {
                    return;
                }
//                mv.visitMethodInsn(INVOKESTATIC, mInjectClazz, "getInject", "()L" + mInjectClazz + ";", false);
                mv.visitLdcInsn(mClazzName);
                boolean is_static = false;
                if ((methodAccess & ACC_STATIC) == 0) {
                    loadThis();
                    is_static = false;
                } else {
                    push((String) null);
                    is_static = true;
                }
                mv.visitLdcInsn(name);
                List<Type> paramsTypeClass = new ArrayList();
                Type[] argsType = Type.getArgumentTypes(desc);
                for (Type type : argsType) {
                    paramsTypeClass.add(type);
                }
//                loadArgArray();
                if(paramsTypeClass.size() == 0){
                    push((String) null);
                }else {
                    AnnaAsmUtils.createObjectArray(mv, paramsTypeClass, is_static);
                }
//                push((String) null);
                mv.visitLdcInsn(Type.getReturnType(methodDesc).toString());
                mv.visitMethodInsn(INVOKESTATIC, mInjectClazz, "onMethodEnter", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/String;)Z", false);
                Label l1 = new Label();
                mv.visitJumpInsn(Opcodes.IFEQ, l1);
                //返回
                mv.visitLdcInsn(mClazzName);
                if ((methodAccess & ACC_STATIC) == 0) {
                    loadThis();
                    is_static = false;
                } else {
                    push((String) null);
                    is_static = true;
                }
                mv.visitLdcInsn(name);
//                loadArgArray();
                if(paramsTypeClass.size() == 0){
                    push((String) null);
                }else {
                    AnnaAsmUtils.createObjectArray(mv, paramsTypeClass, is_static);
                }
//                push((String) null);
                mv.visitLdcInsn(Type.getReturnType(methodDesc).toString());
                mv.visitMethodInsn(INVOKESTATIC, mInjectClazz, "onIntercept", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
                AnnaAsmUtils.returnResult(mv,Type.getReturnType(desc));
                mv.visitLabel(l1);


            }

            @Override
            protected void onMethodExit(int i) {
                if(!mAnnaInject){
                    return;
                }
                //@warn 这部分代码请重点review一下，判断条件写错会要命
                //这部分代码请重点review一下，判断条件写错会要命
                // synthetic 方法暂时不aop 比如AsyncTask 会生成一些同名 synthetic方法,对synthetic 以及private的方法也插入的代码，主要是针对lambda表达式
                if(((access& Opcodes.ACC_SYNTHETIC) != 0)&&((access & Opcodes.ACC_PRIVATE)==0)){
                    return;
                }
                if ((access & Opcodes.ACC_NATIVE) != 0) {
                    return;
                }
                mv.visitLdcInsn(mClazzName);
                boolean is_static = false;
                if ((methodAccess & ACC_STATIC) == 0) {
                    loadThis();
                    is_static = false;
                } else {
                    push((String) null);
                    is_static = true;
                }
                mv.visitLdcInsn(name);
                List<Type> paramsTypeClass = new ArrayList();
                Type[] argsType = Type.getArgumentTypes(desc);
                for (Type type : argsType) {
                    paramsTypeClass.add(type);
                }
//                loadArgArray();
//                if(paramsTypeClass.size() == 0){
                    push((String) null);
//                }else {
//                    AnnaAsmUtils.createObjectArray(mv, paramsTypeClass, is_static);
//                }
                mv.visitLdcInsn(Type.getReturnType(methodDesc).toString());
                mv.visitMethodInsn(INVOKESTATIC, mInjectClazz, "onMethodExit", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/String;)V", false);
            }
        };
        return methodVisitor;

    }
}
