package com.meetyou.anna.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.org.apache.ivy.util.FileUtil
import org.gradle.internal.impldep.org.codehaus.plexus.util.IOUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import sun.instrument.TransformerManager

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

/**
 * Created by Linhh on 17/5/31.
 */

public class PluginImpl extends Transform implements Plugin<Project> {
//    def props = new Properties()
    HashMap<String, ArrayList<AssassinDO>> process = new HashMap<>();
    String mReceiver;

    boolean isLibrary = false;

    private int clazzindex = 1;

    void processFile(String type, String it){
        if(type.equals("receiver")){
            //接收器处理
            mReceiver = it.trim().split("\\;")[0];
            println "anna----> receiver:" + mReceiver
            return;
        }
        ArrayList<AssassinDO> arrayList = process.get(type);
        if(arrayList == null){
            arrayList = new ArrayList<>();
        }
        String m = it.trim().split("\\;")[0];
        AssassinDO assassinDO = new AssassinDO();
        if(!m.trim().startsWith("*")){
            //包名
            String[] r = m.trim().split("\\*");
            assassinDO.des = r[0];//包名
            assassinDO.name = r[1];
        }else {
            String[] r = m.trim().split("\\.");
            if (r[0].trim().equals("**")) {
                //如果是两个*代表是全量
                assassinDO.des = "all";
            } else if (r[0].trim().equals("*")) {
                assassinDO.des = "normal";
            }
            assassinDO.name = r[1];
        }
        arrayList.add(assassinDO);
        println "anna----> annaDO" + assassinDO.toString()
        process.put(type, arrayList);
    }

    void apply(Project project) {
        /*project.task('testTask') << {
             println "Hello gradle plugin!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
         }

         //task耗时监听
         project.gradle.addListener(new TimeListener())
    */
        //读写配置
//        new File("anna.properties").withInputStream {
//            stream -> props.load(stream)
//        }

        String type_default = "default";
        String type_insert = "insert";
        String type_replace = "replace";
        String type_finsert = "finsert";
        String type_freplace = "freplace";
        String type_receiver = "receiver";
        String curr_type = type_default;

        new File("assassin.pro").eachLine {
            if(!it.trim().startsWith("#")) {
                //#开头代表是注释,直接跳过
                if (it.trim().startsWith("}")) {
                    //终止
                    curr_type = type_default;
                }
                if (it.trim().startsWith("-insert")) {
                    //插入
                    curr_type = type_insert;
                } else if (it.trim().startsWith("-replace")) {
                    //替换
                    curr_type = type_replace;
                } else if (it.trim().startsWith("-receiver")) {
                    //监听器
                    curr_type = type_receiver;
                } else if(it.trim().startsWith("-matchreplace")){
                    //完整匹配替换
                    curr_type = type_freplace;
                } else if(it.trim().startsWith("-matchinsert")){
                    //完整匹配插入
                    curr_type = type_finsert;
                }
                //语法体
                if (it.trim().endsWith(";")) {
                    println curr_type + ":" + it
                    if (!curr_type.equals(type_default)) {
                        processFile(curr_type, it)
                    }
                }
            }
        }
        def android = project.extensions.getByType(AppExtension);
        android.registerTransform(this)

        println '==================apply end=================='
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
        if (isLibrary) {
            return TransformManager.SCOPE_FULL_LIBRARY;
//            return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.PROJECT_LOCAL_DEPS);
        }
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

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println '==================anna start=================='
        //配置文件读取
//        Iterator<String> it = props.stringPropertyNames().iterator();
//        ArrayList<AssassinDO> propsItem = new ArrayList<>();
//        String option  = AssassinMethodClassVisitor.OPTION_DEFAULT;
//        while(it.hasNext()){
//            String key = it.next();
//            String v = props[key];
//            //打印配置
//            println key + "=" + v
//            if(key.equals(AssassinMethodClassVisitor.OPTION_NAME)){
//                option = v;
//                continue;
//            }
//            //将配置数据封装
//            AssassinDO assassinDO = new AssassinDO();
//            String[] itemKey = key.split("\\.");
//            assassinDO.mate = itemKey[0];
//            assassinDO.type = itemKey[1];
//            assassinDO.key = itemKey[2];
//            assassinDO.value = v;
//
//            println assassinDO.toString()
//
//            propsItem.add(assassinDO);
//        }
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
                                //这里进行我们的处理 TODO
                                if (name.endsWith(".class") && !name.startsWith("R\$") &&
                                        !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
                                    ClassReader classReader = new ClassReader(file.bytes)
                                    ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
                                    ClassVisitor cv = new AssassinMethodClassVisitor(classWriter, mReceiver, process)
                                    classReader.accept(cv, EXPAND_FRAMES)
                                    byte[] code = classWriter.toByteArray()
                                    FileOutputStream fos = new FileOutputStream(
                                            file.parentFile.absolutePath + File.separator + name)
                                    fos.write(code)
                                    fos.close()
                                    println 'Assassin-----> assassin file:' + file.getAbsolutePath()
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
//                println "Assassin jar jarName:" + jarName + "; "+ jarInput.file.absolutePath
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {

                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
                    String injectClazz = "com/meetyou/anna/inject/support/AnnaInject" + clazzindex;
                    JarFile jarFile = new JarFile(jarInput.file);
                    Enumeration enumeration = jarFile.entries();
                    File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes.jar.tmp");
                    JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile));
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
                                println "entryName:" + entryName
                                jarOutputStream.putNextEntry(zipEntry);
                                ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                                ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
                                ClassVisitor cv = new AnnaJarClassVisitor(injectClazz, Opcodes.ASM5,classWriter)
                                classReader.accept(cv, EXPAND_FRAMES)
                                byte[] code = classWriter.toByteArray()
                                jarOutputStream.write(code);
                            } else {
                                jarOutputStream.putNextEntry(zipEntry);
                                jarOutputStream.write(IOUtils.toByteArray(inputStream));
                            }
                        }
                        jarOutputStream.closeEntry();
                    }
                    //写入inject文件
                    ZipEntry addEntry = new ZipEntry(injectClazz + ".class");
                    AnnaInjectWriter annaInjectWriter = new AnnaInjectWriter();
                    jarOutputStream.putNextEntry(addEntry);
                    jarOutputStream.write(annaInjectWriter.inject(injectClazz));
                    jarOutputStream.closeEntry();

                    clazzindex++ ;
                    //结束
                    jarOutputStream.close();
                    jarFile.close();
                    jarInput.file.delete();
                    tmpFile.renameTo(jarInput.file);
                }
//                println 'Assassin-----> find Jar:' + jarInput.getFile().getAbsolutePath()

                //处理jar进行字节码注入处理 TODO

                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                println 'Assassin-----> copy to Jar:' + dest.absolutePath
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        println '==================anna end=================='

    }
}