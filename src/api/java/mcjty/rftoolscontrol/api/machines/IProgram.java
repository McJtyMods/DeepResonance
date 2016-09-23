package mcjty.rftoolscontrol.api.machines;

import mcjty.rftoolscontrol.api.parameters.Parameter;

import javax.annotation.Nullable;

/**
 * A representation of a program
 */
public interface IProgram {

    /**
     * Set a new 'last value' which can be used by future opcodes
     */
    void setLastValue(Parameter value);

    /**
     * Get the current 'last value'
     */
    Parameter getLastValue();

    /**
     * If this program is running for a craft operation then this will return
     * the current craft ticket.
     */
    @Nullable
    String getCraftTicket();

    public boolean hasCraftTicket();

    /**
     * Suspend the program for a specific number of ticks
     */
    void setDelay(int delay);

    /**
     * Return the remaining time before the program resumes
     */
    int getDelay();

    /**
     * Self-destruct. Call this if you want the program to stop
     */
    void killMe();

    /**
     * Return true if the program will stop
     */
    boolean isDead();
}
