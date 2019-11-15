package com.glorinli.sniper.internal;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class SniperTransform extends Transform {
    @Override
    public String getName() {
        return SniperTransform.class.getSimpleName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        if (!isIncremental()) {
            transformInvocation.getOutputProvider().deleteAll();
        }

        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {
                transformJar(transformInvocation, jarInput);
            }

            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                transformDirectory(transformInvocation, directoryInput);
            }
        }
    }

    /**
     * Transform directory classes
     */
    private void transformDirectory(TransformInvocation invocation, DirectoryInput input) throws IOException {
        File tempDir = invocation.getContext().getTemporaryDir();

        // Get output path
        File dest = invocation.getOutputProvider().getContentLocation(input.getName(),
                input.getContentTypes(), input.getScopes(), Format.DIRECTORY);

        System.out.println("Output path: " + dest.getAbsolutePath());

        File dir = input.getFile();
        if (dir != null && dir.exists()) {
            // Traverse directory and find class files
            Map<String, File> modifiedMap = new HashMap<>();
            traverseDirectory(tempDir, dir, modifiedMap, dir.getAbsolutePath() + File.separator);

            // Copy dir
//            FileUtils.copyDirectory(tempDir, dir);
            for (Map.Entry<String, File> entry : modifiedMap.entrySet()) {
                File target = new File(dest.getAbsolutePath() + File.separator +
                        entry.getKey().replace('.', File.separatorChar) + ".class");
                if (target.exists()) {
                    target.delete();
                }

                System.out.println("Copy to target: " + target.getAbsolutePath());

                // Copy class file
                FileUtils.copyFile(entry.getValue(), target);
            }
        }
    }

    private void traverseDirectory(File tempDir, File dir, Map<String, File> modifiedMap, String baseDirPath) throws IOException {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                traverseDirectory(tempDir, file, modifiedMap, baseDirPath);
            } else if (file.getAbsolutePath().endsWith(".class")) {
                // Handle class file
                byte[] sourceBytes = IOUtils.toByteArray(new FileInputStream(file));
                byte[] modifiedBytes = modifyClass(sourceBytes);

                if (modifiedBytes == null) {
                    modifiedBytes = sourceBytes;
                }

                String className = file.getAbsolutePath().replace(baseDirPath, "")
                        .replace(File.separatorChar, '.').replace(".class", "");

                System.out.println("Class name is: " + className);

                File tempDst = new File(tempDir.getAbsolutePath(), className);
                System.out.println("Temp dst path: " + tempDst);
                if (tempDst.exists()) {
                    tempDst.delete();
                }
                tempDst.createNewFile();

                try (FileOutputStream outputStream = new FileOutputStream(tempDst)) {
                    outputStream.write(modifiedBytes);
                }

                modifiedMap.put(className, tempDst);
            }
        }
    }

    /**
     * Transform jar classes
     */
    private void transformJar(TransformInvocation invocation, JarInput input) throws IOException {
        System.out.println("transformJar: " + input.getName());

        File tempDir = invocation.getContext().getTemporaryDir();

        String destName = input.getFile().getName();
        String hexName = DigestUtils.md5Hex(input.getFile().getAbsolutePath().substring(0, 8));
        if (destName.endsWith(".jar")) {
            destName = destName.substring(0, destName.length() - 4);
        }

        // Get output path
        File dest = invocation.getOutputProvider().getContentLocation(destName + "_" + hexName,
                input.getContentTypes(), input.getScopes(), Format.JAR);
        JarFile originJar = new JarFile(input.getFile());
        File outputJar = new File(tempDir, "temp_" + input.getFile().getName());
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar));

        // Traverse jar
        Enumeration<JarEntry> entries = originJar.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            InputStream inputStream = originJar.getInputStream(jarEntry);
            String entryName = jarEntry.getName();

            if (entryName.endsWith(".class")) {
                JarEntry dstEntry = new JarEntry(entryName);
                jarOutputStream.putNextEntry(dstEntry);

                byte[] sourceBytes = IOUtils.toByteArray(inputStream);

                // Modify class
                byte[] modifiedBytes = modifyClass(sourceBytes);
                if (modifiedBytes == null) {
                    modifiedBytes = sourceBytes;
                }

                jarOutputStream.write(modifiedBytes);
                jarOutputStream.closeEntry();
            }
        }

        jarOutputStream.close();
        originJar.close();
        FileUtils.copyFile(outputJar, dest);
    }

    private byte[] modifyClass(byte[] sourceBytes) {
        // TODO
        return null;
    }
}
