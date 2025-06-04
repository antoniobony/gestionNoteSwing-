package models;

public class Etudiant {
    private Integer numEt;
    private String nom;
    private float moyenne;

    public Etudiant() {}

    public Etudiant(Integer numero, String nom, float moyenne) {
        this.numEt = numero;
        this.nom = nom;
        this.moyenne = moyenne;
    }

    public Integer getNumEt() { return numEt; }
    public void setNumEt(Integer numero) { this.numEt = numero; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public float getMoyenne() { return moyenne; }
    public void setMoyenne(float moyenne) { this.moyenne = moyenne; }

    public String getObservation() {
        if (moyenne >= 10) return "Admis";
        else if (moyenne >= 5) return "Redoublant";
        else return "Exclus";
    }
}
