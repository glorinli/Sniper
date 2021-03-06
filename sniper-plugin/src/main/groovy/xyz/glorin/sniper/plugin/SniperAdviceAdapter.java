package xyz.glorin.sniper.plugin;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class SniperAdviceAdapter extends AdviceAdapter {
    private static final String ANNOTATION_SNIPER_METHOD_TRACK =
            "Lxyz/glorin/sniper/lib/annotations/SniperMethodTrack;";
    private static final String METHOD_EVENT_MANAGER =
            "xyz/glorin/sniper/lib/internal/MethodEventManager";

    private final MethodVisitor methodVisitor;
    private final String methodName;
    private boolean needInject;
    private String tag;

    public SniperAdviceAdapter(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(Opcodes.ASM6, methodVisitor, access, name, desc);
        this.methodVisitor = methodVisitor;
        this.methodName = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor annotationVisitor = super.visitAnnotation(desc, visible);
        if (ANNOTATION_SNIPER_METHOD_TRACK.equals(desc)) {
            needInject = true;

            return new AnnotationVisitor(Opcodes.ASM6, annotationVisitor) {
                @Override
                public void visit(String name, Object value) {
                    super.visit(name, value);

                    if ("tag".equals(name) && value instanceof String) {
                        tag = (String)value;
                    }
                }
            };
        }
        return annotationVisitor;
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();

        if (needInject && tag != null) {
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, METHOD_EVENT_MANAGER,
                    "getInstance", "()L" + METHOD_EVENT_MANAGER + ";", false);
            methodVisitor.visitLdcInsn(tag);
            methodVisitor.visitLdcInsn(methodName);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, METHOD_EVENT_MANAGER,
                    "notifyMethodEnter", "(Ljava/lang/String;Ljava/lang/String;)V", false);
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);

        if (needInject && tag != null) {
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, METHOD_EVENT_MANAGER,
                    "getInstance", "()L" + METHOD_EVENT_MANAGER + ";", false);
            methodVisitor.visitLdcInsn(tag);
            methodVisitor.visitLdcInsn(methodName);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, METHOD_EVENT_MANAGER,
                    "notifyMethodExit", "(Ljava/lang/String;Ljava/lang/String;)V", false);
        }
    }
}
