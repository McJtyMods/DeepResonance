package mcjty.rftoolscontrol.api.machines;

import mcjty.rftoolscontrol.api.code.ICompiledOpcode;
import mcjty.rftoolscontrol.api.code.IOpcodeRunnable;
import mcjty.rftoolscontrol.api.parameters.BlockSide;
import mcjty.rftoolscontrol.api.parameters.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The processor
 */
public interface IProcessor {

    /**
     * Evalulate a parameter with a given index and return an object
     * of the right type or null if the parameter was not given
     */
    @Nullable
    <T> T evaluateParameter(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evalulate a parameter with a given index and return an object
     * of the right type. Gives an exception if the result was null
     */
    @Nonnull
    <T> T evaluateParameterNonNull(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evalulate a parameter with a given index and return an item stack
     * This can convert from String correctly
     * or null if the parameter was not given
     */
    @Nullable
    ItemStack evaluateItemParameter(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evalulate a parameter with a given index and return a BlockSide.
     * This can convert from String correctly
     * Gives an exception if the result was null
     */
    @Nonnull
    ItemStack evaluateItemParameterNonNull(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evalulate a parameter with a given index and return a BlockSide.
     * This can convert from Inventory and String correctly
     * or null if the parameter was not given
     */
    @Nullable
    BlockSide evaluateSideParameter(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evalulate a parameter with a given index and return a BlockSide.
     * This can convert from Inventory and String correctly
     * Gives an exception if the result was null
     */
    @Nonnull
    BlockSide evaluateSideParameterNonNull(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evalulate a parameter with a given index and return an Inventory.
     * This can convert from BlockSide and String correctly
     * or null if the parameter was not given
     */
    @Nullable
    Inventory evaluateInventoryParameter(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evalulate a parameter with a given index and return an Inventory.
     * This can convert from BlockSide and String correctly.
     * Gives an exception if the result was null
     */
    @Nonnull
    Inventory evaluateInventoryParameterNonNull(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evaluate an integer parameter. Return 0 if the parameter was not an integer or null
     */
    int evaluateIntParameter(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evaluate an integer parameter. Return null if the parameter was not given
     */
    @Nullable
    Integer evaluateIntegerParameter(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    @Nullable
    String evaluateStringParameter(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Evaluate a boolean parameter. Return false if the parameter was not given
     */
    boolean evaluateBoolParameter(ICompiledOpcode compiledOpcode, IProgram program, int parIndex);

    /**
     * Set the output redstone power at a given side on the network
     *
     * @param side
     * @param level a value between 0 and 15
     */
    void setPowerOut(@Nonnull BlockSide side, int level);

    /**
     * Read the redstone value at a specific side on the network
     *
     * @param side
     * @return a value between 0 and 15
     */
    int readRedstoneIn(@Nonnull BlockSide side);

    /**
     * Get a tile entity at a specific side of a networked block. If the side in
     * the BlockSide itself is null then the postion is the position of the processor or node itself
     */
    @Nullable
    TileEntity getTileEntityAt(@Nullable BlockSide inv);

    /**
     * Get the block position at a specific side of a networked block. If the side in
     * the BlockSide itself is null then the postion is the position of the processor or node itself
     */
    @Nullable
    BlockPos getPositionAt(@Nullable BlockSide inv);

    /**
     * Get an itemhandler for an inventory at a given position on the network
     */
    IItemHandler getItemHandlerAt(@Nonnull Inventory inv);

    /**
     * Log a message on the console. This message can
     * contain TextFormatting
     */
    void log(String message);

    /**
     * Get an item from an internal slot. The virtualSlot is a slot
     * number relative to how the slots are allocated for the card. So
     * index 0 means the first allocated slot
     */
    @Nullable
    ItemStack getItemInternal(IProgram program, int virtualSlot);

    /**
     * Set a variable to an integer. The index of the variable
     * is relative to how the variables are allocated for the card
     */
    void setVariable(IProgram program, int var);

    /**
     * Get the amount of energy on a given block
     */
    int getEnergy(Inventory side);

    int getMaxEnergy(Inventory side);

    /**
     * Try to place a lock. If it succeeds this returns POSITIVE and
     * the lock will be placed. Otherwise it returns HOLD
     */
    IOpcodeRunnable.OpcodeResult placeLock(String name);

    /**
     * Release the given lock. This does nothing if you don't
     * have the lock.
     */
    void releaseLock(String name);

    /**
     * Test if a given lock is set
     */
    boolean testLock(String name);

    /**
     * Try to request crafting of an item. Returns false if the item
     * could not be requested.
     */
    boolean requestCraft(@Nonnull ItemStack stack, @Nullable Inventory inventory);

    /**
     * Get the amount of liquid on a specific tank. Returns 0 if it is not a tank
     */
    int getLiquid(@Nonnull Inventory side);

    int getMaxLiquid(@Nonnull Inventory side);

    /**
     * Send a signal to this processor to be handled by a program. It returns the
     * number of signal handlers that reacted to this signal.
     */
    int signal(String signal);

    /**
     * If this program is running in the context of a craft operation
     * then you can get the desired craft result here
     */
    @Nullable
    ItemStack getCraftResult(IProgram program);

}