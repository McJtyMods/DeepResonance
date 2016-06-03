package mcjty.deepresonance.items.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.lwjgl.opengl.GL11;

public class RadiationSuitModel extends ModelBiped {

    public static RadiationSuitModel modelBoots;
    public static RadiationSuitModel modelChest;
    public static RadiationSuitModel modelLegs;
    public static RadiationSuitModel modelHelm;

    private ModelRenderer bootsLeft;
    private ModelRenderer bootsRight;


    // Helmet
    ModelRenderer helmettop;
    ModelRenderer helmetside1;
    ModelRenderer helmetside2;
    ModelRenderer helmetbottom;
    ModelRenderer helmetfront1;
    ModelRenderer helmetfront2;
    ModelRenderer helmetfront3;
    ModelRenderer helmetfront4;
    ModelRenderer helmetback;
    ModelRenderer helmetvisor;

    // Boots
    ModelRenderer bootsleftfootbase;
    ModelRenderer bootsrightfootbase;
    ModelRenderer bootsleftback;
    ModelRenderer bootsrightback;
    ModelRenderer bootsleftfront;
    ModelRenderer bootsrightfront;
    ModelRenderer bootsrightside2;
    ModelRenderer bootsleftside2;
    ModelRenderer bootsleftside1;
    ModelRenderer bootsrightside1;
    ModelRenderer bootslefttip;
    ModelRenderer bootsrighttip;

    // Legs
    ModelRenderer legsleftfront;
    ModelRenderer legsrightfront;
    ModelRenderer legsleftback;
    ModelRenderer legsrightback;
    ModelRenderer legsleftside1;
    ModelRenderer legsrightside1;
    ModelRenderer legsleftside2;
    ModelRenderer legsrightside2;

    // Chest
    ModelRenderer chestfront;
    ModelRenderer chestback;
    ModelRenderer chestside1;
    ModelRenderer chestside2;
    ModelRenderer chestlefthand;
    ModelRenderer chestrighthand;
    ModelRenderer chestleftarmside1;
    ModelRenderer chestrightarmside1;
    ModelRenderer chestleftarmside2;
    ModelRenderer chestrightarmside2;
    ModelRenderer chestleftarmfront;
    ModelRenderer chestrightarmfront;
    ModelRenderer chestleftarmback;
    ModelRenderer chestrightarmback;
    ModelRenderer chestleftshoulder;
    ModelRenderer chestrightshoulder;

    public RadiationSuitModel() {
        textureWidth = 64;
        textureHeight = 32;

        setupHelmet();
        setupBoots();
        setupLegs();
        setupChest();
        setupArms();

        bipedHead.addChild(helmetback);
        bipedHead.addChild(helmetbottom);
        bipedHead.addChild(helmetfront1);
        bipedHead.addChild(helmetfront2);
        bipedHead.addChild(helmetfront3);
        bipedHead.addChild(helmetfront4);
        bipedHead.addChild(helmetside1);
        bipedHead.addChild(helmetside2);
        bipedHead.addChild(helmettop);
        bipedHead.addChild(helmetvisor);

        bipedBody.addChild(chestback);
        bipedBody.addChild(chestfront);
        bipedBody.addChild(chestside1);
        bipedBody.addChild(chestside2);

        bipedLeftArm.addChild(chestleftarmback);
        bipedLeftArm.addChild(chestleftarmfront);
        bipedLeftArm.addChild(chestleftarmside1);
        bipedLeftArm.addChild(chestleftarmside2);
        bipedLeftArm.addChild(chestlefthand);
        bipedLeftArm.addChild(chestleftshoulder);

        bipedRightArm.addChild(chestrightarmback);
        bipedRightArm.addChild(chestrightarmfront);
        bipedRightArm.addChild(chestrightarmside1);
        bipedRightArm.addChild(chestrightarmside2);
        bipedRightArm.addChild(chestrighthand);
        bipedRightArm.addChild(chestrightshoulder);

        bipedLeftLeg.addChild(legsleftfront);
        bipedLeftLeg.addChild(legsleftback);
        bipedLeftLeg.addChild(legsleftside1);
        bipedLeftLeg.addChild(legsleftside2);

        bipedRightLeg.addChild(legsrightfront);
        bipedRightLeg.addChild(legsrightback);
        bipedRightLeg.addChild(legsrightside1);
        bipedRightLeg.addChild(legsrightside2);

        bootsLeft = new ModelRenderer(this, 0, 0);
        bootsRight = new ModelRenderer(this, 0, 0);

        bootsLeft.addChild(bootsleftback);
        bootsLeft.addChild(bootsleftfootbase);
        bootsLeft.addChild(bootsleftfront);
        bootsLeft.addChild(bootsleftside1);
        bootsLeft.addChild(bootsleftside2);
        bootsLeft.addChild(bootslefttip);

        bootsRight.addChild(bootsrightback);
        bootsRight.addChild(bootsrightfootbase);
        bootsRight.addChild(bootsrightfront);
        bootsRight.addChild(bootsrightside1);
        bootsRight.addChild(bootsrightside2);
        bootsRight.addChild(bootsrighttip);

        bipedLeftLeg.addChild(bootsLeft);
        bipedRightLeg.addChild(bootsRight);
    }

    private void setupHelmet() {
        helmettop = new ModelRenderer(this, 18, 0);
        helmettop.addBox(0F, 0F, 0F, 8, 1, 8);
        helmettop.setRotationPoint(-4F, -9F, -4F);
        helmettop.setTextureSize(64, 32);
        helmettop.mirror = true;
        setRotation(helmettop, 0F, 0F, 0F);
        helmetside1 = new ModelRenderer(this, 0, 9);
        helmetside1.addBox(0F, 0F, 0F, 1, 8, 8);
        helmetside1.setRotationPoint(4F, -8F, -4F);
        helmetside1.setTextureSize(64, 32);
        helmetside1.mirror = true;
        setRotation(helmetside1, 0F, 0F, 0F);
        helmetside2 = new ModelRenderer(this, 0, 9);
        helmetside2.addBox(0F, 0F, 0F, 1, 8, 8);
        helmetside2.setRotationPoint(-5F, -8F, -4F);
        helmetside2.setTextureSize(64, 32);
        helmetside2.mirror = true;
        setRotation(helmetside2, 0F, 0F, 0F);
        helmetbottom = new ModelRenderer(this, 18, 17);
        helmetbottom.addBox(0F, 0F, 0F, 8, 0, 8);
        helmetbottom.setRotationPoint(-4F, 0F, -4F);
        helmetbottom.setTextureSize(64, 32);
        helmetbottom.mirror = true;
        setRotation(helmetbottom, 0F, 0F, 0F);
        helmetfront1 = new ModelRenderer(this, 18, 9);
        helmetfront1.addBox(0F, 0F, 0F, 8, 2, 1);
        helmetfront1.setRotationPoint(-4F, -8F, -5F);
        helmetfront1.setTextureSize(64, 32);
        helmetfront1.mirror = true;
        setRotation(helmetfront1, 0F, 0F, 0F);
        helmetfront2 = new ModelRenderer(this, 18, 12);
        helmetfront2.addBox(0F, 0F, 0F, 3, 4, 1);
        helmetfront2.setRotationPoint(2F, -6F, -5F);
        helmetfront2.setTextureSize(64, 32);
        helmetfront2.mirror = true;
        setRotation(helmetfront2, 0F, 0F, 0F);
        helmetfront3 = new ModelRenderer(this, 18, 12);
        helmetfront3.addBox(0F, 0F, 0F, 3, 4, 1);
        helmetfront3.setRotationPoint(-5F, -6F, -5F);
        helmetfront3.setTextureSize(64, 32);
        helmetfront3.mirror = true;
        setRotation(helmetfront3, 0F, 0F, 0F);
        helmetfront4 = new ModelRenderer(this, 18, 9);
        helmetfront4.addBox(0F, 0F, 0F, 8, 2, 1);
        helmetfront4.setRotationPoint(-4F, -2F, -5F);
        helmetfront4.setTextureSize(64, 32);
        helmetfront4.mirror = true;
        setRotation(helmetfront4, 0F, 0F, 0F);
        helmetback = new ModelRenderer(this, 0, 0);
        helmetback.addBox(0F, 0F, 0F, 8, 8, 1);
        helmetback.setRotationPoint(-4F, -8F, 4F);
        helmetback.setTextureSize(64, 32);
        helmetback.mirror = true;
        setRotation(helmetback, 0F, 0F, 0F);
        helmetvisor = new ModelRenderer(this, 26, 12);
        helmetvisor.addBox(0F, 0F, 0F, 4, 4, 0);
        helmetvisor.setRotationPoint(-2F, -6F, -4F);
        helmetvisor.setTextureSize(64, 32);
        helmetvisor.mirror = true;
        setRotation(helmetvisor, 0F, 0F, 0F);
    }

    private void setupBoots() {
        float offY = 0;

        bootsleftfootbase = new ModelRenderer(this, 12, 0);
        bootsleftfootbase.addBox(0F, offY, 0F, 4, 0, 4);
        bootsleftfootbase.setRotationPoint(0F, 24F, -2F);
        bootsleftfootbase.setTextureSize(64, 32);
        bootsleftfootbase.mirror = true;
        setRotation(bootsleftfootbase, 0F, 0F, 0F);

        bootsrightfootbase = new ModelRenderer(this, 12, 0);
        bootsrightfootbase.addBox(0F, offY, 0F, 4, 0, 4);
        bootsrightfootbase.setRotationPoint(-4F, 24F, -2F);
        bootsrightfootbase.setTextureSize(64, 32);
        bootsrightfootbase.mirror = true;
        setRotation(bootsrightfootbase, 0F, 0F, 0F);

        bootsleftback = new ModelRenderer(this, 0, 8);
        bootsleftback.addBox(0F, offY, 0F, 4, 4, 2);
        bootsleftback.setRotationPoint(0F, 20F, 2F);
        bootsleftback.setTextureSize(64, 32);
        bootsleftback.mirror = true;
        setRotation(bootsleftback, 0F, 0F, 0F);

        bootsrightback = new ModelRenderer(this, 0, 8);
        bootsrightback.addBox(0F, offY, 0F, 4, 4, 2);
        bootsrightback.setRotationPoint(-4F, 20F, 2F);
        bootsrightback.setTextureSize(64, 32);
        bootsrightback.mirror = true;
        setRotation(bootsrightback, 0F, 0F, 0F);

        bootsleftfront = new ModelRenderer(this, 0, 8);
        bootsleftfront.addBox(0F, offY, 0F, 4, 4, 2);
        bootsleftfront.setRotationPoint(0F, 20F, -4F);
        bootsleftfront.setTextureSize(64, 32);
        bootsleftfront.mirror = true;
        setRotation(bootsleftfront, 0F, 0F, 0F);

        bootsrightfront = new ModelRenderer(this, 0, 8);
        bootsrightfront.addBox(0F, offY, 0F, 4, 4, 2);
        bootsrightfront.setRotationPoint(-4F, 20F, -4F);
        bootsrightfront.setTextureSize(64, 32);
        bootsrightfront.mirror = true;
        setRotation(bootsrightfront, 0F, 0F, 0F);

        bootsrightside2 = new ModelRenderer(this, 0, 0);
        bootsrightside2.addBox(0F, offY, 0F, 2, 4, 4);
        bootsrightside2.setRotationPoint(-2F, 20F, -2F);
        bootsrightside2.setTextureSize(64, 32);
        bootsrightside2.mirror = true;
        setRotation(bootsrightside2, 0F, 0F, 0F);

        bootsleftside2 = new ModelRenderer(this, 0, 0);
        bootsleftside2.addBox(0F, offY, 0F, 2, 4, 4);
        bootsleftside2.setRotationPoint(0F, 20F, -2F);
        bootsleftside2.setTextureSize(64, 32);
        bootsleftside2.mirror = true;
        setRotation(bootsleftside2, 0F, 0F, 0F);

        bootsleftside1 = new ModelRenderer(this, 0, 0);
        bootsleftside1.addBox(0F, offY, 0F, 2, 4, 4);
        bootsleftside1.setRotationPoint(4F, 20F, -2F);
        bootsleftside1.setTextureSize(64, 32);
        bootsleftside1.mirror = true;
        setRotation(bootsleftside1, 0F, 0F, 0F);

        bootsrightside1 = new ModelRenderer(this, 0, 0);
        bootsrightside1.addBox(0F, offY, 0F, 2, 4, 4);
        bootsrightside1.setRotationPoint(-6F, 20F, -2F);
        bootsrightside1.setTextureSize(64, 32);
        bootsrightside1.mirror = true;
        setRotation(bootsrightside1, 0F, 0F, 0F);

        bootslefttip = new ModelRenderer(this, 12, 4);
        bootslefttip.addBox(0F, offY, 0F, 2, 3, 1);
        bootslefttip.setRotationPoint(1F, 21F, -5F);
        bootslefttip.setTextureSize(64, 32);
        bootslefttip.mirror = true;
        setRotation(bootslefttip, 0F, 0F, 0F);

        bootsrighttip = new ModelRenderer(this, 12, 4);
        bootsrighttip.addBox(0F, offY, 0F, 2, 3, 1);
        bootsrighttip.setRotationPoint(-3F, 21F, -5F);
        bootsrighttip.setTextureSize(64, 32);
        bootsrighttip.mirror = true;
        setRotation(bootsrighttip, 0F, 0F, 0F);
    }

    private void setupChest() {
        chestfront = new ModelRenderer(this, 0, 0);
        chestfront.addBox(0F, 0F, 0F, 8, 12, 1);
        chestfront.setRotationPoint(-4F, 0F, -3F);
        chestfront.setTextureSize(64, 32);
        chestfront.mirror = true;
        setRotation(chestfront, 0F, 0F, 0F);

        chestback = new ModelRenderer(this, 0, 0);
        chestback.addBox(0F, 0F, 0F, 8, 12, 1);
        chestback.setRotationPoint(-4F, 0F, 2F);
        chestback.setTextureSize(64, 32);
        chestback.mirror = true;
        setRotation(chestback, 0F, 0F, 0F);

        chestside1 = new ModelRenderer(this, 0, 0);
        chestside1.addBox(0F, 0F, 0F, 1, 12, 8);
        chestside1.setRotationPoint(0F, 4F, -2F);
        chestside1.setTextureSize(64, 32);
        chestside1.mirror = true;
        setRotation(chestside1, 0F, 0F, 0F);

        chestside2 = new ModelRenderer(this, 0, 0);
        chestside2.addBox(0F, 0F, 0F, 1, 12, 8);
        chestside2.setRotationPoint(0F, 8F, -2F);
        chestside2.setTextureSize(64, 32);
        chestside2.mirror = true;
        setRotation(chestside2, 0F, 0F, 0F);
    }

    private void setupLegs() {
        float offX = 1;
        float offY = -12;

        legsleftfront = new ModelRenderer(this, 0, 0);
        legsleftfront.addBox(-offX, offY, 0F, 4, 8, 1);
        legsleftfront.setRotationPoint(0F, 12F, -3F);
        legsleftfront.setTextureSize(64, 32);
        legsleftfront.mirror = true;
        setRotation(legsleftfront, 0F, 0F, 0F);

        legsleftback = new ModelRenderer(this, 0, 0);
        legsleftback.addBox(-offX, offY, 0F, 4, 8, 1);
        legsleftback.setRotationPoint(0F, 12F, 2F);
        legsleftback.setTextureSize(64, 32);
        legsleftback.mirror = true;
        setRotation(legsleftback, 0F, 0F, 0F);

        legsleftside1 = new ModelRenderer(this, 0, 9);
        legsleftside1.addBox(-offX, offY, 0F, 1, 8, 4);
        legsleftside1.setRotationPoint(4F, 12F, -2F);
        legsleftside1.setTextureSize(64, 32);
        legsleftside1.mirror = true;
        setRotation(legsleftside1, 0F, 0F, 0F);

        legsleftside2 = new ModelRenderer(this, 0, 9);
        legsleftside2.addBox(-offX, offY, 0F, 1, 8, 4);
        legsleftside2.setRotationPoint(-1F, 12F, -2F);
        legsleftside2.setTextureSize(64, 32);
        legsleftside2.mirror = true;
        setRotation(legsleftside2, 0F, 0F, 0F);

        legsrightfront = new ModelRenderer(this, 0, 0);
        legsrightfront.addBox(offX, offY, 0F, 4, 8, 1);
        legsrightfront.setRotationPoint(-4F, 12F, -3F);
        legsrightfront.setTextureSize(64, 32);
        legsrightfront.mirror = true;
        setRotation(legsrightfront, 0F, 0F, 0F);

        legsrightback = new ModelRenderer(this, 0, 0);
        legsrightback.addBox(offX, offY, 0F, 4, 8, 1);
        legsrightback.setRotationPoint(-4F, 12F, 2F);
        legsrightback.setTextureSize(64, 32);
        legsrightback.mirror = true;
        setRotation(legsrightback, 0F, 0F, 0F);

        legsrightside1 = new ModelRenderer(this, 0, 9);
        legsrightside1.addBox(offX, offY, 0F, 1, 8, 4);
        legsrightside1.setRotationPoint(0F, 12F, -2F);
        legsrightside1.setTextureSize(64, 32);
        legsrightside1.mirror = true;
        setRotation(legsrightside1, 0F, 0F, 0F);

        legsrightside2 = new ModelRenderer(this, 0, 9);
        legsrightside2.addBox(offX, offY, 0F, 1, 8, 4);
        legsrightside2.setRotationPoint(-5F, 12F, -2F);
        legsrightside2.setTextureSize(64, 32);
        legsrightside2.mirror = true;
        setRotation(legsrightside2, 0F, 0F, 0F);
    }

    private void setupArms() {
        float offX = 5;
        float offY = -1;

        chestlefthand = new ModelRenderer(this, 34, 0);
        chestlefthand.addBox(-offX, offY, 0F, 4, 1, 4);
        chestlefthand.setRotationPoint(4F, 12F, -2F);
        chestlefthand.setTextureSize(64, 32);
        chestlefthand.mirror = true;
        setRotation(chestlefthand, 0F, 0F, 0F);

        chestleftarmside1 = new ModelRenderer(this, 18, 6);
        chestleftarmside1.addBox(-offX, offY, 0F, 1, 12, 4);
        chestleftarmside1.setRotationPoint(3F, 0F, -2F);
        chestleftarmside1.setTextureSize(64, 32);
        chestleftarmside1.mirror = true;
        setRotation(chestleftarmside1, 0F, 0F, 0F);

        chestleftarmside2 = new ModelRenderer(this, 18, 6);
        chestleftarmside2.addBox(-offX, offY, 0F, 1, 12, 4);
        chestleftarmside2.setRotationPoint(8F, 0F, -2F);
        chestleftarmside2.setTextureSize(64, 32);
        chestleftarmside2.mirror = true;
        setRotation(chestleftarmside2, 0F, 0F, 0F);

        chestleftarmfront = new ModelRenderer(this, 28, 6);
        chestleftarmfront.addBox(-offX, offY, 0F, 4, 12, 1);
        chestleftarmfront.setRotationPoint(4F, 0F, -3F);
        chestleftarmfront.setTextureSize(64, 32);
        chestleftarmfront.mirror = true;
        setRotation(chestleftarmfront, 0F, 0F, 0F);

        chestleftarmback = new ModelRenderer(this, 28, 6);
        chestleftarmback.addBox(-offX, offY, 0F, 4, 12, 1);
        chestleftarmback.setRotationPoint(4F, 0F, 2F);
        chestleftarmback.setTextureSize(64, 32);
        chestleftarmback.mirror = true;
        setRotation(chestleftarmback, 0F, 0F, 0F);

        chestleftshoulder = new ModelRenderer(this, 18, 0);
        chestleftshoulder.addBox(-offX, offY, 0F, 4, 2, 4);
        chestleftshoulder.setRotationPoint(4F, -2F, -2F);
        chestleftshoulder.setTextureSize(64, 32);
        chestleftshoulder.mirror = true;
        setRotation(chestleftshoulder, 0F, 0F, 0F);

        chestrighthand = new ModelRenderer(this, 34, 0);
        chestrighthand.addBox(offX, offY, 0F, 4, 1, 4);
        chestrighthand.setRotationPoint(-8F, 12F, -2F);
        chestrighthand.setTextureSize(64, 32);
        chestrighthand.mirror = true;
        setRotation(chestrighthand, 0F, 0F, 0F);

        chestrightarmside1 = new ModelRenderer(this, 18, 6);
        chestrightarmside1.addBox(offX, offY, 0F, 1, 12, 4);
        chestrightarmside1.setRotationPoint(-4F, 0F, -2F);
        chestrightarmside1.setTextureSize(64, 32);
        chestrightarmside1.mirror = true;
        setRotation(chestrightarmside1, 0F, 0F, 0F);

        chestrightarmside2 = new ModelRenderer(this, 18, 6);
        chestrightarmside2.addBox(offX, offY, 0F, 1, 12, 4);
        chestrightarmside2.setRotationPoint(-9F, 0F, -2F);
        chestrightarmside2.setTextureSize(64, 32);
        chestrightarmside2.mirror = true;
        setRotation(chestrightarmside2, 0F, 0F, 0F);

        chestrightarmfront = new ModelRenderer(this, 28, 6);
        chestrightarmfront.addBox(offX, offY, 0F, 4, 12, 1);
        chestrightarmfront.setRotationPoint(-8F, 0F, -3F);
        chestrightarmfront.setTextureSize(64, 32);
        chestrightarmfront.mirror = true;
        setRotation(chestrightarmfront, 0F, 0F, 0F);

        chestrightarmback = new ModelRenderer(this, 28, 6);
        chestrightarmback.addBox(offX, offY, 0F, 4, 12, 1);
        chestrightarmback.setRotationPoint(-8F, 0F, 2F);
        chestrightarmback.setTextureSize(64, 32);
        chestrightarmback.mirror = true;
        setRotation(chestrightarmback, 0F, 0F, 0F);

        chestrightshoulder = new ModelRenderer(this, 18, 0);
        chestrightshoulder.addBox(offX, offY, 0F, 4, 2, 4);
        chestrightshoulder.setRotationPoint(-8F, -2F, -2F);
        chestrightshoulder.setTextureSize(64, 32);
        chestrightshoulder.mirror = true;
        setRotation(chestrightshoulder, 0F, 0F, 0F);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public static ModelBiped getModel(EntityLivingBase entity, ItemStack stack) {

        if (stack == null || !(stack.getItem() instanceof ItemArmor))
            return null;
        EntityEquipmentSlot slot = ((ItemArmor) stack.getItem()).armorType;

        RadiationSuitModel armor;
        if (slot == EntityEquipmentSlot.HEAD && modelHelm != null) {
            return modelHelm;
        } else if (slot == EntityEquipmentSlot.CHEST && modelChest != null) {
            return modelChest;
        } else if (slot == EntityEquipmentSlot.LEGS && modelLegs != null) {
            return modelLegs;
        } else if (slot == EntityEquipmentSlot.FEET && modelBoots != null) {
            return modelBoots;
        }

        armor = new RadiationSuitModel();
        armor.bipedBody.isHidden = true;
        armor.bipedLeftArm.isHidden = true;
        armor.bipedRightArm.isHidden = true;

        armor.bipedHead.isHidden = true;

        armor.bipedLeftLeg.isHidden = true;
        armor.bipedRightLeg.isHidden = true;

        armor.bootsRight.isHidden = true;
        armor.bootsLeft.isHidden = true;

        switch (slot) {
            case HEAD:
                armor.bipedHead.isHidden = false;
                modelHelm = armor;
                break;

            case CHEST:
                armor.bipedBody.isHidden = false;
                armor.bipedLeftArm.isHidden = false;
                armor.bipedRightArm.isHidden = false;
                modelChest = armor;
                break;

            case LEGS:
                armor.bipedLeftLeg.isHidden = false;
                armor.bipedRightLeg.isHidden = false;
                modelLegs = armor;
                break;

            case FEET:
                armor.bootsLeft.isHidden = false;
                armor.bootsRight.isHidden = false;
                modelBoots = armor;
                break;

        }
        return armor;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.isSneak = entity.isSneaking();
        this.isRiding = entity.isRiding();
//        if (entity instanceof EntityLivingBase) {
//            this.isChild = ((EntityLivingBase) entity).isChild();
//            this.rightArmPose = (((EntityLivingBase) entity).getHeldItem(EnumHand.MAIN_HAND) != null ? ArmPose.BOW_AND_ARROW : ArmPose.EMPTY);
//            // TODO possibly check if it can be completely removed? 1.9 thing
//        }

        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        if (this.isChild) {
            float f6 = 2.0F;
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.5F / f6, 1.5F / f6, 1.5F / f6);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            this.bipedHead.render(scale);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            GlStateManager.popMatrix();
        } else {
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            this.bipedHead.render(scale);
            GlStateManager.disableBlend();
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            bootsLeft.rotateAngleX = bipedLeftLeg.rotateAngleX / 2;
            bootsLeft.rotationPointY = -Math.abs((float) Math.sin(bipedLeftLeg.rotateAngleX) * 4);
            bootsLeft.render(scale);
            bootsRight.rotateAngleX = bipedRightLeg.rotateAngleX / 2;
            bootsRight.rotationPointY = -Math.abs((float) Math.sin(bipedRightLeg.rotateAngleX) * 4);
            bootsRight.render(scale);

        }
    }

}
