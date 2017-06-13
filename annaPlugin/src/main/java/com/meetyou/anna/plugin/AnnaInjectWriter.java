package com.meetyou.anna.plugin;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Created by Linhh on 17/6/8.
 */

public class AnnaInjectWriter implements Opcodes{
    public byte[] injectAnnotation(){
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_ANNOTATION + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE, "com/meetyou/anna/inject/support/AnnaInjected", null, "java/lang/Object", new String[] { "java/lang/annotation/Annotation" });

        {
            av0 = cw.visitAnnotation("Ljava/lang/annotation/Retention;", true);
            av0.visitEnum("value", "Ljava/lang/annotation/RetentionPolicy;", "RUNTIME");
            av0.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
    
    public byte[] inject(String clazz, boolean needAnnotation){
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, clazz, null, "java/lang/Object", null);

        {
            if(needAnnotation) {
                av0 = cw.visitAnnotation("Lcom/meetyou/anna/inject/support/AnnaInjected;", true);
                av0.visitEnd();
            }
        }
        {
            fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "ANNA_RECEIVER_CLASS", "Ljava/lang/String;", null, "com.meetyou.anna.client.impl.AnnaReceiver");
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "mAnnaReceiver", "Ljava/lang/Object;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "mMethodEnter", "Ljava/lang/reflect/Method;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "mMethodExit", "Ljava/lang/reflect/Method;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "mOnIntercept", "Ljava/lang/reflect/Method;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "makeClazz", "()V", null, new String[] { "java/lang/Exception" });
            mv.visitCode();
            mv.visitLdcInsn("com.meetyou.anna.client.impl.AnnaReceiver");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "newInstance", "()Ljava/lang/Object;", false);
            mv.visitFieldInsn(PUTSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC + ACC_SYNCHRONIZED, "onMethodEnter", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/String;)Z", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            Label l3 = new Label();
            mv.visitJumpInsn(IFNONNULL, l3);
            mv.visitMethodInsn(INVOKESTATIC, clazz, "makeClazz", "()V", false);
            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitFieldInsn(GETSTATIC, clazz, "mMethodEnter", "Ljava/lang/reflect/Method;");
            Label l4 = new Label();
            mv.visitJumpInsn(IFNONNULL, l4);
            mv.visitFieldInsn(GETSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitLdcInsn("onMethodEnter");
            mv.visitInsn(ICONST_5);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_2);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_3);
            mv.visitLdcInsn(Type.getType("[Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_4);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitFieldInsn(PUTSTATIC, clazz, "mMethodEnter", "Ljava/lang/reflect/Method;");
            mv.visitLabel(l4);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitFieldInsn(GETSTATIC, clazz, "mMethodEnter", "Ljava/lang/reflect/Method;");
            mv.visitFieldInsn(GETSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            mv.visitInsn(ICONST_5);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_3);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_4);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            mv.visitLabel(l1);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(6, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC + ACC_SYNCHRONIZED, "onIntercept", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            Label l3 = new Label();
            mv.visitJumpInsn(IFNONNULL, l3);
            mv.visitMethodInsn(INVOKESTATIC, clazz, "makeClazz", "()V", false);
            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitFieldInsn(GETSTATIC, clazz, "mOnIntercept", "Ljava/lang/reflect/Method;");
            Label l4 = new Label();
            mv.visitJumpInsn(IFNONNULL, l4);
            mv.visitFieldInsn(GETSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitLdcInsn("onIntercept");
            mv.visitInsn(ICONST_5);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_2);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_3);
            mv.visitLdcInsn(Type.getType("[Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_4);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitFieldInsn(PUTSTATIC, clazz, "mOnIntercept", "Ljava/lang/reflect/Method;");
            mv.visitLabel(l4);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitFieldInsn(GETSTATIC, clazz, "mOnIntercept", "Ljava/lang/reflect/Method;");
            mv.visitFieldInsn(GETSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            mv.visitInsn(ICONST_5);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_3);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_4);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitLabel(l1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(6, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC + ACC_SYNCHRONIZED, "onMethodExit", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/String;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            Label l3 = new Label();
            mv.visitJumpInsn(IFNONNULL, l3);
            mv.visitMethodInsn(INVOKESTATIC, clazz, "makeClazz", "()V", false);
            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitFieldInsn(GETSTATIC, clazz, "mMethodExit", "Ljava/lang/reflect/Method;");
            Label l4 = new Label();
            mv.visitJumpInsn(IFNONNULL, l4);
            mv.visitFieldInsn(GETSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitLdcInsn("onMethodExit");
            mv.visitInsn(ICONST_5);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_2);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_3);
            mv.visitLdcInsn(Type.getType("[Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_4);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitFieldInsn(PUTSTATIC, clazz, "mMethodExit", "Ljava/lang/reflect/Method;");
            mv.visitLabel(l4);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitFieldInsn(GETSTATIC, clazz, "mMethodExit", "Ljava/lang/reflect/Method;");
            mv.visitFieldInsn(GETSTATIC, clazz, "mAnnaReceiver", "Ljava/lang/Object;");
            mv.visitInsn(ICONST_5);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_3);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_4);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(POP);
            mv.visitLabel(l1);
            Label l5 = new Label();
            mv.visitJumpInsn(GOTO, l5);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            mv.visitLabel(l5);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(6, 6);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();

    }
}
