package kademicsapp.kademicsapp.View;

public class viewManager {

    public void showAlunoView() {
        alunoView alunoView = new alunoView(); 
        alunoView.alunoViews(); 
    }

    public void showProfessorView() {
        professorView professorView = new professorView(); 
        professorView.professorMenu(); 
    }

    public void showTreinoView() {
        treinoView treinoView = new treinoView(); 
        treinoView.treinoMenu(); 
    }

    public void showMensalidadeView() {
        mensalidadeView mensalidadeView = new mensalidadeView(); 
        mensalidadeView.mensalidadeMenu(); 
    }
}
