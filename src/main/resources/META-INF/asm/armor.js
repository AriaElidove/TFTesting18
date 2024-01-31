// noinspection ES6ConvertVarToLetConst

var ASM = Java.type('net.neoforged.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

// noinspection JSUnusedGlobalSymbols
function initializeCoreMod() {
    return {
        'armor': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer',
                'methodName': 'renderArmorPiece',
                'methodDesc': '(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                instructions.insert(
                    ASM.findFirstInstruction(methodNode, Opcodes.INSTANCEOF),
                    ASM.listOf(
                        new VarInsnNode(Opcodes.ALOAD, 7),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            'twilightforest/ASMHooks',
                            'cancelArmorRendering',
                            '(ZLnet/minecraft/world/item/ItemStack;)Z',
                            false
                        )
                    )
                );
                return methodNode;
            }
        }
    }
}
