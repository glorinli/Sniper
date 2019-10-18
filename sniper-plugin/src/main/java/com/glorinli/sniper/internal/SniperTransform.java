package com.glorinli.sniper.internal;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.gradle.internal.impldep.org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

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
    private void transformDirectory(TransformInvocation invocation, DirectoryInput input) {

    }

    /**
     * Transform jar classes
     */
    private void transformJar(TransformInvocation invocation, JarInput input) {
        File tempDir = invocation.getContext().getTemporaryDir();

        String destName = input.getFile().getName();
        String hexName = DigestUtils.md5Hex(input.getFile().getAbsolutePath().substring(0, 8));
        if (destName.endsWith(".jar")) {
            destName = destName.substring(0, destName.length() - 4);
        }

        // Get output path
    }
}
