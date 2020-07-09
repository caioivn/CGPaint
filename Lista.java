import java.util.ArrayList;

/*
Estrutura de dados utilizada para armazenamento dos desenhos: Lista encadeada
*/
public class Lista{
    public Celula primeiro,ultimo;
	 
    public Lista(){
	primeiro=ultimo=null;
    }
	 
    public void inserir(ArrayList<desenho> x){
        if(ultimo==null){
            primeiro=ultimo=new Celula(x);
	}
	else{
            ultimo.prox=new Celula(x);
            ultimo=ultimo.prox;
	}
    }
    
    public void remover(Celula x){
        Celula i = primeiro;
        while(i != null){
            if(i == x && i == ultimo){
                i = null;
            }
            else if(i == x && i!=ultimo){
                i = i.prox.prox;
            }
            if(i!=null){
                i = i.prox;
            }
        }
    }

    /*A lista perde a referencia de seus objetos para "deletar" os objetos
     ate o coletor de lixo do java realmente deleta-los*/
    public void clear(){
	primeiro=ultimo=null;
    }
		
    /*Retorna o tamanho da lista (quantidade de elementos armazenados)*/
    public int tamanho(){
        int resp=0;
        for(Celula i=primeiro;i!=null;i=i.prox){
            resp++;
	}
	return resp;
    }
}
