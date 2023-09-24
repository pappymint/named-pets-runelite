package com.pappymint.namedpets;

public class NamedPet {
    public NamedPet(String petName, int npcId) {
        this.petName = petName;
        this.npcId = npcId;
    }

    public final String petName;

    public final int npcId;
}
