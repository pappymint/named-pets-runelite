package com.pappymint.namedpets;

public class NamedPet {
    public NamedPet(String petName, int npcId, String petNameHexColor) {
        this.petName = petName;
        this.npcId = npcId;
        this.petNameHexColor = petNameHexColor;
    }

    public final String petName;

    public final int npcId;

    public final String petNameHexColor;
}
