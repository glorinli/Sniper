package xyz.glorin.sniper.plugin;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SniperClassVisitor extends ClassVisitor {

    public SniperClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        return new SniperAdviceAdapter(methodVisitor, access, name, desc);
    }
}
