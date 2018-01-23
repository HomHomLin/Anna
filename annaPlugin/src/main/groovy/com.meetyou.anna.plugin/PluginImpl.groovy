package com.meetyou.anna.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.meetyou.anna.ConfigurationDO
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AnnotationNode

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

/**
 * Created by Linhh on 17/5/31.
 */

public class PluginImpl extends Transform implements Plugin<Project> {

    private MeetyouConfiguration meetyouConfiguration = new MeetyouConfiguration("anna_list.pro");
    private ArrayList<ConfigurationDO> mInclude;
    private int clazzindex = 1;
    private ArrayList<String> mBuildDirs = new ArrayList<>();
    private String mInjectPkg = "com/meetyou/anna/inject/support";
    private String minjectClass = "AnnaProjectInject"
    private boolean mNeedInject = false;
    //metas
    private HashMap<String, ArrayList<String>> mMetas = new HashMap<>();

    void apply(Project project) {
        println '==============anna apply start=================='
        //读取当前工程的build dir
        project.android.applicationVariants.all {
            it ->
                println "find build dir: $project.buildDir/intermediates/classes/${it.dirName}"
                mBuildDirs.add("$project.buildDir/intermediates/classes/${it.dirName}");

        }

        //处理配置文件

        meetyouConfiguration.process();
        meetyouConfiguration.print();
        mInclude = meetyouConfiguration.map.get("include");
        ArrayList<ConfigurationDO> switch_list = meetyouConfiguration.map.get("switch");
        if(switch_list != null){
            mNeedInject = switch_list.get(0).strings[0].equals("all");
        }

        println "need inject=" + mNeedInject


        def android = project.extensions.getByType(AppExtension);
        android.registerTransform(this)

        println '==============anna apply end=================='
    }


    @Override
    public String getName() {
        return "Anna";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
    }

    void processMetas(AnnaClassVisitor cv){
        if(cv.mAnnotationList != null) {
            for (AnnotationNode annotationNode : cv.mAnnotationList) {
                List vl = annotationNode.values;
                if (vl != null) {
                    //vl.get(0).toString() 属性名字
                    ArrayList<String> metaClazz = mMetas.get(vl.get(1));
                    if (metaClazz == null){
                        metaClazz = new ArrayList<>();
                    }
                    if(!metaClazz.contains(cv.mClazzName)){
                        metaClazz.add(cv.mClazzName);
                    }
                    mMetas.put(vl.get(1), metaClazz);
                    println "anna annotation:" + cv.mClazzName + ';' + vl.get(0).toString() + "=" + vl.get(1)
                }
            }
        }
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println '==================anna transform start=================='
        //写入inject数据
        AnnaInjectWriter annaInjectWriter = new AnnaInjectWriter();
        for(String buildDir : mBuildDirs){
            String pkg = buildDir + File.separator + mInjectPkg;
            File pkgFile = new File(pkg);
            pkgFile.mkdirs()

            //写入注解
            FileOutputStream fos = new FileOutputStream(pkg + File.separator + "AnnaInjected.class");
            fos.write(annaInjectWriter.injectAnnotation())
            fos.close()

            //写入inject类
            fos = new FileOutputStream(pkg + File.separator + minjectClass + ".class");
            fos.write(annaInjectWriter.inject(mInjectPkg + "/" + minjectClass, true))
            fos.close()


        }
        if(outputProvider!=null)
            outputProvider.deleteAll()
        //遍历inputs里的TransformInput
        inputs.each { TransformInput input ->
            //遍历input里边的DirectoryInput
            input.directoryInputs.each {
                DirectoryInput directoryInput ->
                    //是否是目录
                    if (directoryInput.file.isDirectory()) {
                        //遍历目录
                        directoryInput.file.eachFileRecurse {
                            File file ->
                                def filename = file.name;
                                def name = file.name
                                String injectClazz = mInjectPkg + "/" + minjectClass
                                //这里进行我们的处理 TODO
                                if (name.endsWith(".class") && !name.startsWith("R\$") &&
                                        !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
                                    ClassReader classReader = new ClassReader(file.bytes)
                                    ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
                                    ClassVisitor cv = new AnnaClassVisitor(injectClazz, Opcodes.ASM5,classWriter,mNeedInject,mInclude)
                                    classReader.accept(cv, EXPAND_FRAMES)
                                    byte[] code = classWriter.toByteArray()
                                    FileOutputStream fos = new FileOutputStream(
                                            file.parentFile.absolutePath + File.separator + name)
                                    fos.write(code)
                                    fos.close()

                                    processMetas(cv);
                                    println 'Anna-----> inject file:' + file.getAbsolutePath()
                                }
//                                println 'Assassin-----> find file:' + file.getAbsolutePath()
                                //project.logger.
                        }
                    }
                    //处理完输入文件之后，要把输出给下一个任务
                    def dest = outputProvider.getContentLocation(directoryInput.name,
                            directoryInput.contentTypes, directoryInput.scopes,
                            Format.DIRECTORY)
                    FileUtils.copyDirectory(directoryInput.file, dest)
            }


            input.jarInputs.each { JarInput jarInput ->
                /**
                 * 重名名输出文件,因为可能同名,会覆盖
                 */
                def jarName = jarInput.name
                println "Anna jarName:" + jarName + "; "+ jarInput.file.absolutePath
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {

                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                File tmpFile = null;
                if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
                    String injectClazz =  mInjectPkg + "/" + minjectClass;
                    JarFile jarFile = new JarFile(jarInput.file);
                    Enumeration enumeration = jarFile.entries();
                    tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_anna.jar");
                    //避免上次的缓存被重复插入
                    if(tmpFile.exists()) {
                        tmpFile.delete();
                    }
                    JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile));
                    //用于保存
                    ArrayList<String> processorList = new ArrayList<>();
                    while (enumeration.hasMoreElements()) {
                        JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                        String entryName = jarEntry.getName();
                        ZipEntry zipEntry = new ZipEntry(entryName);

                        InputStream inputStream = jarFile.getInputStream(jarEntry);
                        //如果是inject文件就跳过
                        if(!entryName.startsWith("com/meetyou/anna/inject/support/AnnaInject")){
                            //anna插桩class
                            if (entryName.endsWith(".class") && !entryName.contains("R\$") &&
                                    !entryName.contains("R.class") && !entryName.contains("BuildConfig.class")) {
                                //class文件处理
                                println "entryName anna:" + entryName
                                jarOutputStream.putNextEntry(zipEntry);
                                ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                                ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
                                ClassVisitor cv = new AnnaClassVisitor(injectClazz, Opcodes.ASM5,classWriter,mNeedInject,mInclude)
                                classReader.accept(cv, EXPAND_FRAMES)
                                byte[] code = classWriter.toByteArray()
                                jarOutputStream.write(code);
                                processMetas(cv);
                            } else if(entryName.contains("META-INF/services/javax.annotation.processing.Processor")){
                                if(!processorList.contains(entryName)){
                                    println "entryName no anna:" + entryName
                                    processorList.add(entryName)
                                    jarOutputStream.putNextEntry(zipEntry);
                                    jarOutputStream.write(IOUtils.toByteArray(inputStream));
                                }else{
                                    println "duplicate entry:" + entryName
                                }
                            }else {
                                println "entryName no anna:" + entryName
                                jarOutputStream.putNextEntry(zipEntry);
                                jarOutputStream.write(IOUtils.toByteArray(inputStream));
                            }
                        }
                        jarOutputStream.closeEntry();
                    }
                    //写入inject注解

                    //写入inject文件
//                    ZipEntry addEntry = new ZipEntry(injectClazz + ".class");
//                    jarOutputStream.putNextEntry(addEntry);
//                    jarOutputStream.write(annaInjectWriter.inject(injectClazz,false));
//                    jarOutputStream.closeEntry();
//
//                    clazzindex++ ;
                    //结束
                    jarOutputStream.close();
                    jarFile.close();
//                    jarInput.file.delete();
//                    tmpFile.renameTo(jarInput.file);
                }
//                println 'Assassin-----> find Jar:' + jarInput.getFile().getAbsolutePath()

                //处理jar进行字节码注入处理 TODO

                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                println 'Anna-----> copy to Jar:' + dest.absolutePath
                if(tmpFile == null) {
                    FileUtils.copyFile(jarInput.file, dest)
                }else{
                    FileUtils.copyFile(tmpFile, dest)
                    tmpFile.delete()
                }
            }
        }

        //创建meta数据
        File meta_file = outputProvider.getContentLocation("anna_inject_metas", getOutputTypes(), getScopes(),
                Format.JAR);
        if(!meta_file.getParentFile().exists()){
            meta_file.getParentFile().mkdirs();
        }
        if(meta_file.exists()){
            meta_file.delete();
        }

//        JarFile jarFile = new JarFile(meta_file);
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(meta_file));
        ZipEntry addEntry = new ZipEntry("com/meetyou/anna/inject/support/AnnaInjectMetas.class");
        jarOutputStream.putNextEntry(addEntry);
        jarOutputStream.write(annaInjectWriter.makeMetas("com/meetyou/anna/inject/support/AnnaInjectMetas",mMetas));
        jarOutputStream.closeEntry();
        //结束
        jarOutputStream.close();
//        jarFile.close();

        println '==================Anna transform end=================='

    }
}