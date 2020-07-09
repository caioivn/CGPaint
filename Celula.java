import java.util.ArrayList;

 /*
 Estrutura utilizada como se fosse o desenho propriamente dito, aqui sÃ£o armazenados
 os pontos de um determinado desenho e sua cor. No caso da circunferÃªncia, armazenamos
 o ponto do centro da circunferÃªncia e seu tipo (representando ser um circunferÃªncia).
 */

public class Celula{
    public ArrayList<desenho> elemento;
    public Celula prox;
    public int tipo,xc,yc,cor;
	 
    public Celula(ArrayList <desenho> elemento){
	this.elemento=elemento;
	this.prox=null;
	tipo=0;
	xc=0;
	yc=0;
	cor=1;
    }
}
