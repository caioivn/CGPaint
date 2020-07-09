import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Robot;


public class CGPaint extends javax.swing.JFrame {
    /**
     * Creates new form CGPaint
     */
    public CGPaint() {
        initComponents();
        Borracha();
        start();
        addMouseListener(new MouseListener() {
            /*
             Caso o mouse for pressionado, apagamos as referencias do desenho anterior
             para que um nao se conecte ao outro, e habilitamos a variavel pressionado
             que ira fazer com que uma figura seja desenhada, apenas se o mouse for pressionado 
             */
            public void mousePressed(MouseEvent e) {
                pressionado = true;
                desenhos.clear();
            }

            /*
             Armazenamos o desenho livre apenas quando o mouse nao esta mais sendo pressionado,
             pois o desenho livre e o unico tipo de desenho neste software que plota os pontos
             em tempo real. Ou seja, os outros metodos apenas irao plotar os desenhos
             depois que o mouse for pressionado e liberado
             */
            public void mouseReleased(MouseEvent e) {
                if (desenho_livre) {
                    quadro.inserir(desenhos);
                    desenhos.clear();
                }
                pressionado = false;
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                Point p = getMousePosition();
                /*
                 Metodos que utilizam a variavel click para encontrar qual objeto
                 o usuario deseja manipular
                 */
                if (p.y >= 195 && (translacao || rotacao || reflexao || escala || bezier || preenchimento || hermite || interpolacao)) {
                    click = true;
                } else {
                    click = false;
                }
            }
        });
        new time().start();
    }
    
    public void start(){
        try{
            setTitle("CG Paint");
            image=new BufferedImage(getWidth(),700,BufferedImage.TYPE_INT_RGB);
            robot = new Robot();
            setSize(getWidth(),700);
            setLocationRelativeTo(null);
            setResizable(false);
            matrizInterpolacao[0][0] = 1;
            matrizInterpolacao[0][1] = 0;
            matrizInterpolacao[0][2] = 0;
            matrizInterpolacao[0][3] = 0;
            matrizInterpolacao[1][0] = -5.5;
            matrizInterpolacao[1][1] = 9;
            matrizInterpolacao[1][2] = -4.5;
            matrizInterpolacao[1][3] = 1;
            matrizInterpolacao[2][0] = 9;
            matrizInterpolacao[2][1] = -22.5;
            matrizInterpolacao[2][2] = 18;
            matrizInterpolacao[2][3] = -4.5;
            matrizInterpolacao[3][0] = -4.5;
            matrizInterpolacao[3][1] = 13.5;
            matrizInterpolacao[3][2] = -13.5;
            matrizInterpolacao[3][3] = 4.5;
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            btn_okBezier.setVisible(false);
            btn_cima.setVisible(false);
            btn_baixo.setVisible(false);
            btn_esquerda.setVisible(false);
            btn_direita.setVisible(false);
            btn_rotacionar.setVisible(false);
            btn_mais.setVisible(false);
            btn_menos.setVisible(false);
            btn_okTransformacao.setVisible(false);
        }
        catch(Exception e){
        
        }
    }
    
    /*
     Metodo onde sao feitos os plots dos desenhos
     */
    public void desenhar(Graphics g) {
        g.setColor(Color.BLACK);
        /*
         Le o arquivo quadro.txt e recupera os desenhos que foram salvos
         */
        g.create();
        if (load) {
            File file = new File("quadro.txt");
            String line = null;
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                else{
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    while ((line = br.readLine()) != null) {
                        ArrayList<desenho> tmp = new ArrayList<>();
                        quadro.inserir(tmp);
                        quadro.ultimo.cor = Integer.parseInt(line);
                        line = br.readLine();   
                        if(line.length() > 0){
                            String coordenadas[] = line.split(" ");
                            for (int i = 0; i < coordenadas.length; i++) {
                                String pontos[] = coordenadas[i].split(",");
                                quadro.ultimo.elemento.add(new desenho(Integer.parseInt(pontos[0]), Integer.parseInt(pontos[1])));
                                if (quadro.ultimo.cor == 0) {
                                    g.setColor(Color.RED);
                                }
                                else if (quadro.ultimo.cor == 1) {
                                    g.setColor(Color.BLACK);
                                }
                                else if (quadro.ultimo.cor == 2) {
                                    g.setColor(Color.GREEN);
                                }
                                else if (quadro.ultimo.cor == 3) {
                                    g.setColor(Color.BLUE);
                                }
                                else if (quadro.ultimo.cor == 4) {
                                    g.setColor(Color.YELLOW);
                                }
                                if(Integer.parseInt(pontos[1]) >= 195){
                                    g.drawLine(Integer.parseInt(pontos[0]), Integer.parseInt(pontos[1]), Integer.parseInt(pontos[0]), Integer.parseInt(pontos[1]));
                                }
                            }
                        }
                    }
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*
         Caso o botao Limpar Quadro for selecionado, a opcao clear ira limpar o quadro
         */
        else if (clear) {
            g.clearRect(0, 195, getWidth(), 700);
            if(quadro.tamanho() > 0){
                quadro.clear();
            }
            clear = false;
        }
	/* Caso o botao Curvas Interpoladas for selecionado, o if abaixo será executado:
	 */
        else if(interpolacao){
            /* Quando o usuario clicar na area de desenho, capturamos o ponto de controle desejado.
	        */
            if(click && contPontos < 4){
                if(contPontos == 0){
                    ArrayList<desenho> tmp = new ArrayList<>();
                    quadro.inserir(tmp);
                    quadro.ultimo.cor = cor;
                }
                Point pointer = getMousePosition();
                pontos[contPontos] = new desenho(pointer.x,pointer.y);
                contPontos++;
                click = false;
            }
            /* Quando o usuario clicar 4 vezes na area de desenho, ou seja,
               quando o usuario escolher todos os pontos de controle desejados, a curva e plotada.
	        */
            else if(contPontos == 4){
                double c = 0, d = 0, x, y;
                for(int i = 0; i < 4; i++){
                    for(int j = 0; j < 4; j++){
                        c = c +(matrizInterpolacao[i][j] * pontos[j].x);
                        d = d +(matrizInterpolacao[i][j] * pontos[j].y);
                    }
                }
                for(double t = 0;t <= 1;t = t+0.001){
                    x = c * Math.pow(t,3) + c * Math.pow(t,2) + c * t + c;
                    y = d * Math.pow(t,3) + d * Math.pow(t,2) + d * t + d;
                    //o desenho e plotado de acordo com a cor desejada
                    if (quadro.ultimo.cor == 0) {
                        g.setColor(Color.RED);
                    }
                    else if (quadro.ultimo.cor == 1) {
                        g.setColor(Color.BLACK);
                    }
                    else if (quadro.ultimo.cor == 2) {
                        g.setColor(Color.GREEN);
                    }
                    else if (quadro.ultimo.cor == 3) {
                        g.setColor(Color.BLUE);
                    }
                    else if (quadro.ultimo.cor == 4) {
                        g.setColor(Color.YELLOW);
                    }
                    //o desenho e plotado dentro da area de desenho (abaixo dos botoes)
                    if(y >= 195){
                        quadro.ultimo.elemento.add(new desenho ((int)x,(int)y));
                        g.drawLine((int)x, (int)y, (int)x, (int)y);
                    }
                }
                contPontos = 0;
            }
        }
	/* Caso o botao Curvas de Hermite for selecionado, o if abaixo será executado:
	 */
        else if(hermite){
            /* Quando o usuario clicar na area de desenho, capturamos o ponto de controle desejado.
	        */
            if(click && contPontos < 3){
                if(contPontos == 0){
                    ArrayList<desenho> tmp = new ArrayList<>();
                    quadro.inserir(tmp);
                    quadro.ultimo.cor = cor;
                }
                Point pointer = getMousePosition();
                pontosHermite[contPontos] = new desenho(pointer.x,pointer.y);
                contPontos++;
                click = false;
            }
            /* Quando o usuario clicar 3 vezes na area de desenho, ou seja,
               quando o usuario escolher todos os pontos de controle desejados, a curva e plotada.
	        */
            else if(contPontos == 3){
                int dp0x = pontosHermite[2].x - pontosHermite[0].x, dp1x = pontosHermite[1].x - pontosHermite[2].x;
                int dp0y = pontosHermite[2].y - pontosHermite[0].y, dp1y = pontosHermite[1].y - pontosHermite[2].y;
                int px,py;
                double x,y;
                px = (2 * (pontosHermite[0].x - pontosHermite[1].x)) + dp0x + dp1x;
                py = (2 * (pontosHermite[0].y - pontosHermite[1].y)) + dp0y + dp1y;
                pontos[0] = new desenho(px,py); 
                px = (-3 * (pontosHermite[0].x - pontosHermite[1].x)) - (2 * dp0x) - dp1x;
                py = (-3 * (pontosHermite[0].y - pontosHermite[1].y)) - (2 * dp0y) - dp1y;
                pontos[1] = new desenho(px,py);
                px = dp0x;
                py = dp0y;
                pontos[2] = new desenho(px,py);
                px = pontosHermite[0].x;
                py = pontosHermite[0].y;
                pontos[3] = new desenho(px,py);
                for(double t = 0;t <= 1;t = t+0.001){
                    x = pontos[0].x * Math.pow(t,3) + pontos[1].x * Math.pow(t,2) + pontos[2].x * t + pontos[3].x;
                    y = pontos[0].y * Math.pow(t,3) + pontos[1].y * Math.pow(t,2) + pontos[2].y * t + pontos[3].y;
                    //o desenho e plotado de acordo com a cor desejada
                    if (quadro.ultimo.cor == 0) {
                        g.setColor(Color.RED);
                    }
                    else if (quadro.ultimo.cor == 1) {
                        g.setColor(Color.BLACK);
                    }
                    else if (quadro.ultimo.cor == 2) {
                        g.setColor(Color.GREEN);
                    }
                    else if (quadro.ultimo.cor == 3) {
                        g.setColor(Color.BLUE);
                    }
                    else if (quadro.ultimo.cor == 4) {
                        g.setColor(Color.YELLOW);
                    }
                    //o desenho e plotado dentro da area de desenho (abaixo dos botoes)
                    if(y >= 195){
                        quadro.ultimo.elemento.add(new desenho ((int)x,(int)y));
                        g.drawLine((int)x, (int)y, (int)x, (int)y);
                    }
                }
                contPontos = 0;
            }
        }
	/* Caso o botao Preencher Interpoladas for selecionado, o if abaixo será executado:
	 */
        else if(preenchimento){
            /* Quando o usuario clicar na area de desenho, capturamos o ponto onde a partir deste ponto
               iremos colorir os pixels do desenho desejado.
	        */
            if(click){
                Point pointer = getMousePosition();
                colorO = image.getRGB(pointer.x,pointer.y);
                oldColor = robot.getPixelColor(pointer.x,pointer.y);
                //o desenho e preenchido de acordo com a cor desejada
                if(cor == 0){
                    colorN = Color.red.getRGB();
                    newColor = Color.red;
                }
                else if(cor == 1){
                    colorN = Color.black.getRGB();
                    newColor = Color.black;
                }
                else if(cor==2){
                    colorN = Color.green.getRGB();
                    newColor = Color.green;
                }
                else if(cor == 3){
                    colorN = Color.blue.getRGB();
                    newColor = Color.blue;
                }
                else if(cor == 4){
                    colorN = Color.yellow.getRGB();
                    newColor = Color.yellow;
                }
                preencher(pointer.x, pointer.y,g);
                click = false;
            }
        }
	/* Caso o botao Curvas de Bezier for selecionado, o if abaixo será executado:
	 */
        else if(bezier){
            /* Quando o usuario clicar na area de desenho, capturamos o ponto de controle desejado.
	        */
            if(click && contPontos < 4){
                if(contPontos == 0){
                    ArrayList<desenho> tmp = new ArrayList<>();
                    quadro.inserir(tmp);
                    quadro.ultimo.cor = cor;
                }
                Point pointer = getMousePosition();
                pontos[contPontos] = new desenho(pointer.x,pointer.y);
                contPontos++;
                click = false;
            }
            /* Diferentemente dos metodos de Curvas Interpoladas e Curvas de Hermite, este metodo permite
               que o usuario realize o plot das curvas com 2 ou 3 ou 4 pontos de controle. Para isso, apos
               selecionar os pontos de controle desjados, o usuario plota a curva clicando no botao Plot Bezier.
	        */
            else if(plotBezier && contPontos > 1){
                double x0,y0,x1,y1,x,y;
                //Caso o usuario escolha 2 pontos de controle:
                if(contPontos == 2){
                    for(double t = 0; t <= 1; t = t + 0.001){
                        x = ((1-t)*pontos[0].x) + (t*pontos[1].x);
                        y = ((1-t)*pontos[0].y) + (t*pontos[1].y);
                        //o desenho e plotado de acordo com a cor desejada
                        if (quadro.ultimo.cor == 0) {
                            g.setColor(Color.RED);
                        }
                        else if (quadro.ultimo.cor == 1) {
                            g.setColor(Color.BLACK);
                        }
                        else if (quadro.ultimo.cor == 2) {
                            g.setColor(Color.GREEN);
                        }
                        else if (quadro.ultimo.cor == 3) {
                            g.setColor(Color.BLUE);
                        }
                        else if (quadro.ultimo.cor == 4) {
                            g.setColor(Color.YELLOW);
                        }
                        //o desenho e plotado dentro da area de desenho (abaixo dos botoes)
                        if(y >= 195){
                            quadro.ultimo.elemento.add(new desenho ((int)x,(int)y));
                            g.drawLine((int)x, (int)y, (int)x, (int)y);
                        }
                    }
                    contPontos = 0;
                }
                //Caso o usuario escolha 3 pontos de controle:
                else if(contPontos == 3){
                    for(double t = 0; t <= 1; t = t + 0.001){
                        x = (Math.pow((1-t),2)*pontos[0].x) + (2*(1-t)*t*pontos[1].x)+(Math.pow(t,2)*pontos[2].x);
                        y = (Math.pow((1-t),2)*pontos[0].y) + (2*(1-t)*t*pontos[1].y)+(Math.pow(t,2)*pontos[2].y);
                        //o desenho e plotado de acordo com a cor desejada
                        if (quadro.ultimo.cor == 0) {
                            g.setColor(Color.RED);
                        }
                        else if (quadro.ultimo.cor == 1) {
                            g.setColor(Color.BLACK);
                        }
                        else if (quadro.ultimo.cor == 2) {
                            g.setColor(Color.GREEN);
                        }
                        else if (quadro.ultimo.cor == 3) {
                            g.setColor(Color.BLUE);
                        }
                        else if (quadro.ultimo.cor == 4) {
                            g.setColor(Color.YELLOW);
                        }
                        //o desenho e plotado dentro da area de desenho (abaixo dos botoes)
                        if(y >= 195){
                            quadro.ultimo.elemento.add(new desenho ((int)x,(int)y));
                            g.drawLine((int)x, (int)y, (int)x, (int)y);
                        }
                    }
                    contPontos = 0;
                }
                //Caso o usuario escolha 4 pontos de controle:
                else{
                    for(double t = 0; t<=1;t=t+0.001){
                        x0 = (Math.pow((1-t),2)*pontos[0].x) + (2*(1-t)*t*pontos[1].x)+(Math.pow(t,2)*pontos[2].x);
                        y0 = (Math.pow((1-t),2)*pontos[0].y) + (2*(1-t)*t*pontos[1].y)+(Math.pow(t,2)*pontos[2].y);
                        x1 = (Math.pow((1-t),2)*pontos[1].x) + (2*(1-t)*t*pontos[2].x)+(Math.pow(t,2)*pontos[3].x);
                        y1 = (Math.pow((1-t),2)*pontos[1].y) + (2*(1-t)*t*pontos[2].y)+(Math.pow(t,2)*pontos[3].y);
                        x = (1-t)*x0+(t*x1);
                        y = (1-t)*y0+(t*y1);
                        //o desenho e plotado de acordo com a cor desejada
                        if (quadro.ultimo.cor == 0) {
                            g.setColor(Color.RED);
                        }
                        else if (quadro.ultimo.cor == 1) {
                            g.setColor(Color.BLACK);
                        }
                        else if (quadro.ultimo.cor == 2) {
                            g.setColor(Color.GREEN);
                        }
                        else if (quadro.ultimo.cor == 3) {
                            g.setColor(Color.BLUE);
                        }
                        else if (quadro.ultimo.cor == 4) {
                            g.setColor(Color.YELLOW);
                        }
                        //o desenho e plotado dentro da area de desenho (abaixo dos botoes)
                        if(y >= 195){
                            quadro.ultimo.elemento.add(new desenho ((int)x,(int)y));
                            g.drawLine((int)x, (int)y, (int)x, (int)y);
                        }
                    }
                    contPontos = 0;
                }
                plotBezier = false;
            }
            
        }
        /*
         Caso o botao translacao for selecionado, executaremos o if abaixo
         */
        else if (translacao) {
            int passos = 5;
            /*
            Caso o usuario clique na tela (dentro da regiao valida), ou seja,
            dentro da janela de desenho e abaixo da regiao dos botoes
             */
            if (click) {
                Point pointer = getMousePosition();
                /*
                 Buscamos o objeto selecionado e que sera transladado
                 */
                for (Celula i = quadro.primeiro; i != null; i = i.prox) {
                    for (int j = 0; j < i.elemento.size(); j++) {
                        if ((i.elemento.get(j).x <= pointer.x + 2 && i.elemento.get(j).x >= pointer.x - 2) && (i.elemento.get(j).y <= pointer.y + 2 && i.elemento.get(j).y >= pointer.y - 2)) {
                            k = i;
                            i = quadro.ultimo;
                            j = i.elemento.size();
                            click = false;
                        }
                    }
                }
            }
            else if(k!=null){
                xInicio = k.elemento.get(0).x;
                yInicio = k.elemento.get(0).y;
                xFim = k.elemento.get(k.elemento.size() - 1).x;
                yFim = k.elemento.get(k.elemento.size() - 1).y;
                /*
                 Move o objeto para cima
                 */
                if (cima) {
                    /*
                     Colorimos os pontos do objeto em questao de branco
                     antes de caminharmos com eles
                     */
                    g.setColor(Color.WHITE);
                    for (int i = 0; i < k.elemento.size(); i++) {
                        if(k.elemento.get(i).y>=195){
                            g.drawLine(k.elemento.get(i).x, k.elemento.get(i).y, k.elemento.get(i).x, k.elemento.get(i).y);
                        }
                        k.elemento.get(i).y = k.elemento.get(i).y - passos;
                    }
                    plotPontos(g);
                    cima = false;
                }
                /*
                 Move o objeto para baixo
                 */
                else if (baixo) {
                    /*
                     Colorimos os pontos do objeto em questao de branco
                     antes de caminharmos com eles
                     */
                    g.setColor(Color.WHITE);
                    for (int i = 0; i < k.elemento.size(); i++) {
                        if(k.elemento.get(i).y>=195){
                            g.drawLine(k.elemento.get(i).x, k.elemento.get(i).y, k.elemento.get(i).x, k.elemento.get(i).y);
                        }
                        k.elemento.get(i).y = k.elemento.get(i).y + passos;
                    }
                    plotPontos(g);
                    baixo = false;
                }
                /*
                 Move o objeto para direita
                 */
                else if (direita) {
                    /*
                     Colorimos os pontos do objeto em questao de branco
                     antes de caminharmos com eles
                     */
                    g.setColor(Color.WHITE);
                    for (int i = 0; i < k.elemento.size(); i++) {
                        if(k.elemento.get(i).y>=195){
                            g.drawLine(k.elemento.get(i).x, k.elemento.get(i).y, k.elemento.get(i).x, k.elemento.get(i).y);
                        }
                        k.elemento.get(i).x = k.elemento.get(i).x + passos;
                    }
                    plotPontos(g);
                    direita = false;
                }
                /*
                 Move o objeto para esquerda
                 */
                else if (esquerda) {
                    /*
                     Colorimos os pontos do objeto em questao de branco
                     antes de caminharmos com eles
                     */
                    g.setColor(Color.WHITE);
                    for (int i = 0; i < k.elemento.size(); i++) {
                        //g.setColor(Color.WHITE);
                        if(k.elemento.get(i).y>=195){
                            g.drawLine(k.elemento.get(i).x, k.elemento.get(i).y, k.elemento.get(i).x, k.elemento.get(i).y);
                        }
                        k.elemento.get(i).x = k.elemento.get(i).x - passos;
                    }
                    plotPontos(g);
                    esquerda = false;
                }
            }
        }
        /*
         Caso o botao rotacao for selecionado, executaremos o if abaixo
         */
        else if (rotacao) {
            /*
             Caso o usuario clique na tela (dentro da regiao valida), ou seja,
             dentro da janela de desenho e abaixo da regiao dos botoes
             */
            if (click) {
                Point pointer = getMousePosition();
                /*
                 Buscamos o objeto selecionado e que sera rotacionado
                 */
                for (Celula i = quadro.primeiro; i != null; i = i.prox) {
                    for (int j = 0; j < i.elemento.size(); j++) {
                        if ((i.elemento.get(j).x <= pointer.x + 2 && i.elemento.get(j).x >= pointer.x - 2) && (i.elemento.get(j).y <= pointer.y + 2 && i.elemento.get(j).y >= pointer.y - 2)) {
                            k = i;
                            i = quadro.ultimo;
                            j = i.elemento.size();
                            click = false;
                            xInicio = k.elemento.get(0).x;
                            yInicio = k.elemento.get(0).y;
                            xFim = k.elemento.get(k.elemento.size() - 1).x;
                            yFim = k.elemento.get(k.elemento.size() - 1).y;
                        }
                    }
                }
            }
            else if(k != null){
                /*
                 Rotaciona o objeto
                 */
                if (rotacionar) {
                    /*
                     Colorimos os pontos do objeto em questao de branco
                     antes de rotacionarmos eles
                    */
                    g.setColor(Color.WHITE);
                    for (int i = 0; i < k.elemento.size(); i++) {
                        if(k.elemento.get(i).y>=195){
                            g.drawLine(k.elemento.get(i).x, k.elemento.get(i).y, k.elemento.get(i).x, k.elemento.get(i).y);
                        }
                    }
                    //Rotacao em 15 graus
                    for (int i = 0; i < k.elemento.size(); i++) {
                        int xAux = (int) ((k.elemento.get(i).x * Math.cos(Math.PI / 12)) + (k.elemento.get(i).y * -(Math.sin(Math.PI / 12))));
                        int yAux = (int) ((k.elemento.get(i).x * Math.sin(Math.PI / 12)) + (k.elemento.get(i).y * Math.cos(Math.PI / 12)));
                        k.elemento.get(i).x = xAux;
                        k.elemento.get(i).y = yAux;
                    }
                    /*
                     Reposicionameno do desenho
                     */
                    while (k.elemento.get(0).x < xInicio) {
                        for (int i = 0; i < k.elemento.size(); i++) {
                            k.elemento.get(i).x++;
                        }
                    }
                    while (k.elemento.get(0).x > xInicio) {
                        for (int i = 0; i < k.elemento.size(); i++) {
                            k.elemento.get(i).x--;
                        }
                    }
                    while (k.elemento.get(0).y < yInicio) {
                        for (int i = 0; i < k.elemento.size(); i++) {
                            k.elemento.get(i).y++;
                        }
                    }
                    while (k.elemento.get(0).y > yInicio) {
                        for (int i = 0; i < k.elemento.size(); i++) {
                            k.elemento.get(i).y--;
                        }
                    }
                    /*
                     Plot dos pontos
                     */
                    plotPontos(g);
                    rotacionar = false;
                }
            }
        }
        /*
         Caso o botao reflexao for selecionado, executaremos o if abaixo
         */
        else if (reflexao) {
            /*
             Caso o usuario clique na tela (dentro da regiao valida), ou seja,
             dentro da janela de desenho e abaixo da regiao dos botoes
             */
            if (click) {
                Point pointer = getMousePosition();
                /*
                 Buscamos o objeto selecionado e que sera refletido
                 */
                for (Celula i = quadro.primeiro; i != null; i = i.prox) {
                    for (int j = 0; j < i.elemento.size(); j++) {
                        if ((i.elemento.get(j).x <= pointer.x + 2 && i.elemento.get(j).x >= pointer.x - 2) && (i.elemento.get(j).y <= pointer.y + 2 && i.elemento.get(j).y >= pointer.y - 2)) {
                            k = i;
                            i = quadro.ultimo;
                            j = i.elemento.size();
                            click = false;
                            refletir = true;
                        }
                    }
                }
            }
            /*
             Reflete o objeto
            */
            else if (refletir) {
                xInicio = k.elemento.get(0).x;
                yInicio = k.elemento.get(0).y;
                xFim = k.elemento.get(k.elemento.size() - 1).x;
                yFim = k.elemento.get(k.elemento.size() - 1).y;
                /*
                 Colorimos os pontos do objeto em questao de branco
                 antes de refletirmos eles
                 */
                g.setColor(Color.WHITE);
                for (int i = 0; i < k.elemento.size(); i++) {
                    if(k.elemento.get(i).y>=195){
                        g.drawLine(k.elemento.get(i).x, k.elemento.get(i).y, k.elemento.get(i).x, k.elemento.get(i).y);
                    }
                }
                for (int i = 0; i < k.elemento.size(); i++) {
                    int xAux = (int) ((k.elemento.get(i).x * Math.cos(Math.PI)) + (k.elemento.get(i).y * -(Math.sin(Math.PI))));
                    int yAux = (int) ((k.elemento.get(i).x * Math.sin(Math.PI)) + (k.elemento.get(i).y * Math.cos(Math.PI)));
                    k.elemento.get(i).x = xAux;
                    k.elemento.get(i).y = yAux;
                }
                /*
                 Reposicionameno do desenho
                 */
                while (k.elemento.get(0).x < xInicio) {
                    for (int i = 0; i < k.elemento.size(); i++) {
                        k.elemento.get(i).x++;
                    }
                }
                while (k.elemento.get(0).x > xInicio) {
                    for (int i = 0; i < k.elemento.size(); i++) {
                        k.elemento.get(i).x--;
                    }
                }
                while (k.elemento.get(0).y < yInicio) {
                    for (int i = 0; i < k.elemento.size(); i++) {
                        k.elemento.get(i).y++;
                    }
                }
                while (k.elemento.get(0).y > yInicio) {
                    for (int i = 0; i < k.elemento.size(); i++) {
                        k.elemento.get(i).y--;
                    }
                }
                /*
                 Plot dos pontos
                 */
                plotPontos(g);
                refletir = false;
            }
        }
        /*
         Caso o botao escala for selecionado, executaremos o if abaixo
         */
        else if (escala) {
            /*
             Caso o usuario clique na tela (dentro da regiao valida), ou seja,
             dentro da janela de desenho e abaixo da regiao dos botoes
             */
            if (click) {
                Point pointer = getMousePosition();
                /*
                 Buscamos o objeto desejado
                 */
                for (Celula i = quadro.primeiro; i != null; i = i.prox) {
                    for (int j = 0; j < i.elemento.size(); j++) {
                        if ((i.elemento.get(j).x <= pointer.x + 2 && i.elemento.get(j).x >= pointer.x - 2) && (i.elemento.get(j).y <= pointer.y + 2 && i.elemento.get(j).y >= pointer.y - 2)) {
                            k = i;
                            i = quadro.ultimo;
                            j = i.elemento.size();
                            click = false;
                        }
                    }
                }
            }
            else if(k != null){
                xInicio = k.elemento.get(0).x;
                yInicio = k.elemento.get(0).y;
                /*
                 Aumenta o tamanho do objeto
                 */
                if (mais) {
                    /*
                     Colorimos os pontos do objeto em questao de branco
                     antes de aumentarmos eles
                     */
                    g.setColor(Color.WHITE);
                    for (int i = 0; i < k.elemento.size(); i++) {
                        g.drawLine(k.elemento.get(i).x, k.elemento.get(i).y, k.elemento.get(i).x, k.elemento.get(i).y);
                    }
                    //O objeto e aumentado em mais 50% em relacao ao seu tamanho inicial
                    for (int i = 0; i < k.elemento.size(); i++) {
                        k.elemento.get(i).x = (int) (k.elemento.get(i).x * 1.5);
                        k.elemento.get(i).y = (int) (k.elemento.get(i).y * 1.5);
                        if (k.tipo == 1) {
                            k.xc = (int) (k.xc * 1.5);
                            k.yc = (int) (k.yc * 1.5);
                        }
                    }
                    /*
                     Reposicionameno do desenho
                    */
                   if (k.tipo != 1) {
                       while (k.elemento.get(0).x < xInicio) {
                           for (int i = 0; i < k.elemento.size(); i++) {
                               k.elemento.get(i).x++;
                           }
                       }
                       while (k.elemento.get(0).x > xInicio) {
                           for (int i = 0; i < k.elemento.size(); i++) {
                               k.elemento.get(i).x--;
                           }
                       }
                       while (k.elemento.get(0).y < yInicio) {
                           for (int i = 0; i < k.elemento.size(); i++) {
                               k.elemento.get(i).y++;
                           }
                       }
                       while (k.elemento.get(0).y > yInicio) {
                           for (int i = 0; i < k.elemento.size(); i++) {
                               k.elemento.get(i).y--;
                           }
                       }
                   }
                   /*
                    Plot dos pontos
                    */
                   plotPontos(g);
                    mais = false;
                }
                /*
                 Diminui o tamanho do objeto
                 */
                else if (menos) {
                    /*
                     Colorimos os pontos do objeto em questao de branco
                     antes de diminuirmos eles
                     */
                    g.setColor(Color.WHITE);
                    for (int i = 0; i < k.elemento.size(); i++) {
                        g.drawLine(k.elemento.get(i).x, k.elemento.get(i).y, k.elemento.get(i).x, k.elemento.get(i).y);
                    }
                    //Reduzimos o objeto pela metade
                    for (int i = 0; i < k.elemento.size(); i++) {
                        k.elemento.get(i).x = (int) (k.elemento.get(i).x * 0.5);
                        k.elemento.get(i).y = (int) (k.elemento.get(i).y * 0.5);
                        if (k.tipo == 1) {
                            k.xc = (int) (k.xc * 0.5);
                            k.yc = (int) (k.yc * 0.5);
                        }
                    }
                    /*
                     Reposicionameno do desenho
                    */
                   if (k.tipo != 1) {
                       while (k.elemento.get(0).x < xInicio) {
                           for (int i = 0; i < k.elemento.size(); i++) {
                               k.elemento.get(i).x++;
                           }
                       }
                       while (k.elemento.get(0).x > xInicio) {
                           for (int i = 0; i < k.elemento.size(); i++) {
                               k.elemento.get(i).x--;
                           }
                       }
                       while (k.elemento.get(0).y < yInicio) {
                           for (int i = 0; i < k.elemento.size(); i++) {
                               k.elemento.get(i).y++;
                           }
                       }
                       while (k.elemento.get(0).y > yInicio) {
                           for (int i = 0; i < k.elemento.size(); i++) {
                               k.elemento.get(i).y--;
                           }
                       }
                   }
                   /*
                    Plot dos pontos
                    */
                   plotPontos(g);
                    menos = false;
                }
                
            }
        }
        /*
         O algoritmo inicia executando este metodo, e caso o botao desenho livre
         for selecionado, executaremos o if abaixo
         */
        else if (desenho_livre) {
            for (int i = 1; i < desenhos.size(); i++) {
                int x = desenhos.get(i).x;
                int y = desenhos.get(i).y;
                int x2 = desenhos.get(i - 1).x;
                int y2 = desenhos.get(i - 1).y;
                //o desenho e plotado de acordo com a cor desejada
                if (cor == 0) {
                    g.setColor(Color.RED);
                }
                else if (cor == 1) {
                    g.setColor(Color.BLACK);
                }
                else if (cor == 2) {
                    g.setColor(Color.GREEN);
                }
                else if (cor == 3) {
                    g.setColor(Color.BLUE);
                }
                else if (cor == 4) {
                    g.setColor(Color.YELLOW);
                }
                g.drawLine(x2, y2, x, y);
            }
        }
        /*
         Caso o botao borracha for selecionado, executaremos o if abaixo
         */
        else if (borracha) {
            for (int i = 0; i < desenhos.size(); i++) {
                int x = desenhos.get(i).x;
                int y = desenhos.get(i).y;
                if(quadro.tamanho()>0){
                    quadro.clear();
                }
                g.clearRect(x, y, 50, 50);
            }
        }
        /*
        Executamos os if's abaixo somente se o botao do mouse nao estiver pressionado,
        pois coletamo os pontos que serao usados, e o tamanho das retas (DDA e Bresenhan)
        alem de coletarmos o tamanho desejado para a circunferencia, retangulo e area para
        recorte
        */
        else if (!pressionado) {
            int x1 = 1,y1 = 1,x2 = 2,y2 = 2,dx,dy;
            if (!desenhos.isEmpty()) {
                x1 = desenhos.get(0).x;
                y1 = desenhos.get(0).y;
                x2 = desenhos.get(desenhos.size() - 1).x;
                y2 = desenhos.get(desenhos.size() - 1).y;
            }
            /*
             Caso o botao DDA for selecionado, executaremos o if abaixo
             */
            if (dda_abilitado) {
                /*
                Com o intuito de evitar alocar inumeros desenhos vazios na lista,
                verificamos se o array list desenhos encontra-se preenchido
                e alocamos um novo elemento na lista de desenhos
                 */
                if (!desenhos.isEmpty()) {
                    ArrayList<desenho> tmp = new ArrayList<>();
                    quadro.inserir(tmp);
                    quadro.ultimo.cor = cor;
                    desenhos.clear();
                    int passos, k;
                    float x_incr, y_incr, x, y;
                    dx = x2 - x1;
                    dy = y2 - y1;
                    if (Math.abs(dx) > Math.abs(dy)) {
                        passos = Math.abs(dx);
                    } else {
                        passos = Math.abs(dy);
                    }
                    x_incr = dx / passos;
                    y_incr = dy / passos;
                    x = x1;
                    y = y1;
                    //o desenho e plotado de acordo com a cor desejada
                    if (cor == 0) {
                        g.setColor(Color.RED);
                    }
                    else if (cor == 1) {
                        g.setColor(Color.BLACK);
                    }
                    else if (cor == 2) {
                        g.setColor(Color.GREEN);
                    }
                    else if (cor == 3) {
                        g.setColor(Color.BLUE);
                    }
                    else if (cor == 4) {
                        g.setColor(Color.YELLOW);
                    }
                    //plot do ponto
                    g.drawLine((int) (Math.round(x) / 1.0), (int) (Math.round(y) / 1.0), (int) (Math.round(x) / 1.0), (int) (Math.round(y) / 1.0));
                    //armazenamos o ponto na lista de desenhos
                    quadro.ultimo.elemento.add(new desenho((int) (Math.round(x) / 1.0), (int) (Math.round(y) / 1.0)));
                    for (k = 1; k <= passos; k++) {
                        x = x + x_incr;
                        y = y + y_incr;
                        //armazenamos o ponto na lista de desenhos
                        quadro.ultimo.elemento.add(new desenho((int) (Math.round(x) / 1.0), (int) (Math.round(y) / 1.0)));
                        //plot do ponto
                        g.drawLine((int) (Math.round(x) / 1.0), (int) (Math.round(y) / 1.0), (int) (Math.round(x) / 1.0), (int) (Math.round(y) / 1.0));
                    }
                }         
            }
            /*
             Caso o botao Bresenhan for selecionado, executaremos o if abaixo
             */
            else if (bresenhan_abilitado) {
                /*
                 Com o intuito de evitar alocar inÃºmeros desenhos vazios na lista,
                 verificamos se o array list desenhos encontra-se preenchido
                 e alocamos um novo elemento na lista de desenhos
                 */
                if (!desenhos.isEmpty()) {
                    ArrayList<desenho> tmp = new ArrayList<>();
                    quadro.inserir(tmp);
                    quadro.ultimo.cor = cor;
                    desenhos.clear();
                    int x, y, const1, const2, p, incrx, incry, i;
                    dx = x2 - x1;
                    dy = y2 - y1;
                    if (dx >= 0) {
                        incrx = 1;
                    }
                    else {
                        incrx = -1;
                        dx = -dx;
                    }
                    if (dy >= 0) {
                        incry = 1;
                    }
                    else {
                        incry = -1;
                        dy = -dy;
                    }
                    x = x1;
                    y = y1;
                    //o desenho e plotado de acordo com a cor desejada
                    if (cor == 0) {
                        g.setColor(Color.RED);
                    }
                    else if (cor == 1) {
                        g.setColor(Color.BLACK);
                    }
                    else if (cor == 2) {
                        g.setColor(Color.GREEN);
                    }
                    else if (cor == 3) {
                        g.setColor(Color.BLUE);
                    }
                    else if (cor == 4) {
                        g.setColor(Color.YELLOW);
                    }
                    //plot do ponto
                    g.drawLine(x, y, x, y);
                    //armazenamos o ponto na lista de desenhos
                    quadro.ultimo.elemento.add(new desenho(x, y));
                    if (dy < dx) {
                        p = 2 * dy - dx;
                        const1 = 2 * dy;
                        const2 = 2 * (dy - dx);
                        for (i = 0; i < dx; i++) {
                            x += incrx;
                            if (p < 0) {
                                p += const1;
                            }
                            else {
                                y += incry;
                                p += const2;
                            }
                            //armazenamos o ponto na lista de desenhos
                            quadro.ultimo.elemento.add(new desenho(x, y));
                            //plot do ponto
                            g.drawLine(x, y, x, y);
                        }
                    }
                    else {
                        p = 2 * dx - dy;
                        const1 = 2 * dx;
                        const2 = 2 * (dx - dy);
                        for (i = 0; i < dy; i++) {
                            y += incry;
                            if (p < 0) {
                                p += const1;
                            }
                            else {
                                x += incrx;
                                p += const2;
                            }
                            //armazenamos o ponto na lista de desenhos
                            quadro.ultimo.elemento.add(new desenho(x, y));
                            //plot do ponto
                            g.drawLine(x, y, x, y);
                        }
                    }
                }
                
            }
            /*
             Caso o botao Circunferencia for selecionado, executaremos o if abaixo
             */
            else if (circunferencia_abilitado) {
                /*
                 Com o intuito de evitar alocar inumeros desenhos vazios na lista,
                 verificamos se o array list desenhos encontra-se preenchido
                 e alocamos um novo elemento na lista de desenhos
                 */
                if (!desenhos.isEmpty()) {
                    ArrayList<desenho> tmp = new ArrayList<>();
                    quadro.inserir(tmp);
                    quadro.ultimo.cor = cor;
                    int xc = desenhos.get(0).x, yc = desenhos.get(0).y, p, x = desenhos.get(desenhos.size() - 1).x, y = desenhos.get(desenhos.size() - 1).y, r;
                    quadro.ultimo.tipo = 1;
                    quadro.ultimo.xc = xc;
                    quadro.ultimo.yc = yc;
                    dx = x2 - x1;
                    dy = y2 - y1;
                    r = (int) (Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2))));
                    //armazenamos os pontos na lista de desenhos
                    inserePontos(xc, yc, x, y);
                    //o desenho e plotado de acordo com a cor desejada
                    if (cor == 0) {
                        g.setColor(Color.RED);
                    }
                    else if (cor == 1) {
                        g.setColor(Color.BLACK);
                    }
                    else if (cor == 2) {
                        g.setColor(Color.GREEN);
                    }
                    else if (cor == 3) {
                        g.setColor(Color.BLUE);
                    } else if (cor == 4) {
                        g.setColor(Color.YELLOW);
                    }
                    //plot dos pontos
                    plotCircunferencia(g,xc,yc,x,y);
                    x = 0;
                    y = r;
                    p = 3 - 2 * r;
                    //armazenamos os pontos na lista de desenhos
                    inserePontos(xc, yc, x, y);
                    //plot dos pontos
                    plotCircunferencia(g,xc,yc,x,y);
                    while (x < y) {
                        if (p < 0) {
                            p = p + 4 * x + 6;
                        }
                        else {
                            p = p + 4 * (x - y) + 10;
                            y = y - 1;
                        }
                        x = x + 1;
                        //armazenamos os pontos na lista de desenhos
                        inserePontos(xc, yc, x, y);
                        //plot dos pontos
                        plotCircunferencia(g,xc,yc,x,y);
                    }
                    desenhos.clear();
                }
            }
            /*
             Caso o botao Retangulos for selecionado, executaremos o if abaixo
             */
            else if (retangulo) {
                /*
                 Com o intuito de evitar alocar inumeros desenhos vazios na lista,
                 verificamos se o array list desenhos encontra-se preenchido
                 e alocamos um novo elemento na lista de desenhos
                 */
                if (!desenhos.isEmpty()) {
                    ArrayList<desenho> tmp = new ArrayList<>();
                    quadro.inserir(tmp);
                    quadro.ultimo.cor = cor;
                    desenhos.clear();
                     //o desenho e plotado de acordo com a cor desejada
                    if (cor == 0) {
                        g.setColor(Color.RED);
                    }
                    else if (cor == 1) {
                        g.setColor(Color.BLACK);
                    }
                    else if (cor == 2) {
                        g.setColor(Color.GREEN);
                    }
                    else if (cor == 3) {
                        g.setColor(Color.BLUE);
                    }
                    else if (cor == 4) {
                        g.setColor(Color.YELLOW);
                    }
                    for (int i = 0; i < Maior(x1, x2) - Menor(x1, x2); i++) {
                        //plot do ponto
                        g.drawLine(Menor(x1, x2) + i, Menor(y1, y2), Menor(x1, x2) + i, Menor(y1, y2));
                        //armazenamos o ponto da lista de desenhos
                        quadro.ultimo.elemento.add(new desenho(Menor(x1, x2) + i, Menor(y1, y2)));
                    }
                    for (int i = 0; i < Maior(y1, y2) - Menor(y1, y2); i++) {
                        //plot do ponto
                        g.drawLine(Menor(x1, x2), Menor(y1, y2) + i, Menor(x1, x2), Menor(y1, y2) + i);
                        //armazenamos o ponto na lista de desenhos
                        quadro.ultimo.elemento.add(new desenho(Menor(x1, x2), Menor(y1, y2) + i));
                    }
                    for (int i = 0; i < Maior(y1, y2) - Menor(y1, y2); i++) {
                        //plot do ponto
                        g.drawLine(Maior(x1, x2), Menor(y1, y2) + i, Maior(x1, x2), Menor(y1, y2) + i);
                        //armazenamos o ponto na lista de desnhos
                        quadro.ultimo.elemento.add(new desenho(Maior(x1, x2), Menor(y1, y2) + i));
                    }
                    for (int i = 0; i < Maior(x1, x2) - Menor(x1, x2); i++) {
                        //plot do ponto
                        g.drawLine(Menor(x1, x2) + i, Maior(y1, y2), Menor(x1, x2) + i, Maior(y1, y2));
                        //armazenamos o ponto na lista de desenhos
                        quadro.ultimo.elemento.add(new desenho(Menor(x1, x2) + i, Maior(y1, y2)));
                    }
                }
            }
            /*
             Caso o botao Recorte for selecionado, executaremos o if abaixo
             */
            else if (recorte) {
                /*
                 Em cada desenho no quadro, os pontos que estiverem fora
                 da area selecionada sao removidos
                 */
                if (!desenhos.isEmpty()) {
                    for (Celula c = quadro.primeiro; c != null; c = c.prox) {
                        for (int i = 0; i < c.elemento.size(); i++) {
                            if (c.elemento.get(i).x < Menor(x1, x2)) {
                                c.elemento.remove(i);
                            }
                            else if (c.elemento.get(i).x > Maior(x1, x2)) {
                                c.elemento.remove(i);
                            }
                        }
                        for(int i = 0; i < c.elemento.size(); i++){
                            if (c.elemento.get(i).y < Menor(y1, y2)) {
                                c.elemento.remove(i);
                            }
                            else if (c.elemento.get(i).y > Maior(y1, y2)) {
                                c.elemento.remove(i);
                            }
                        }
                    }
                    //apaga regiao acima da area selecionada
                    g.clearRect(0, 195, getWidth(), (Menor(y1, y2) - 195));
                    //apaga a regiao abaixo da area selecionada
                    g.clearRect(0, Maior(y1, y2), getWidth(), (700 - Maior(y1, y2)));
                    //apaga a regiao a  direita da area selecionada
                    g.clearRect(Maior(x1, x2), 195, (getWidth() - Maior(x1, x2)), 505);
                    //apaga a regiao a  esquerda da area selecionada
                    g.clearRect(0, 195, Menor(x1, x2), 505);
                }
            }
            /*
             Caso o botao Cohen-Sutherland for selecionado, executaremos o if abaixo
             */
            else if (cohen) {
                if (!desenhos.isEmpty()) {
                    int cfora, xInt = 0, yInt = 0;
                    xMin = Menor(x1, x2);
                    yMin = Menor(y1, y2);
                    xMax = Maior(x1, x2);
                    yMax = Maior(y1, y2);
                    for (Celula i = quadro.primeiro; i != null; i = i.prox) {
                        x01 = i.elemento.get(0).x;
                        y01 = i.elemento.get(0).y;
                        x02 = i.elemento.get(i.elemento.size() - 1).x;
                        y02 = i.elemento.get(i.elemento.size() - 1).y;
                        boolean aceite = false, feito = false;
                        while (!feito) {
                            int c1 = regionCode(x01, y01);
                            int c2 = regionCode(x02, y02);
                            if (c1 == 0 && c2 == 0) {
                                aceite = true;
                                feito = true;
                            }
                            else if (c1 != 0 && c2 != 0) {
                                feito = true;
                            }
                            else {
                                if (c1 != 0) {
                                    cfora = c1;
                                }
                                else {
                                    cfora = c2;
                                }
                                if (cfora == 1 || cfora == 5 || cfora == 9) {
                                    xInt = xMin;
                                    yInt = y01 + (y02 - y01) * (xMin - x01) / (x02 - x01);
                                }
                                else if (cfora == 2 || cfora == 10 || cfora == 6) {
                                    xInt = xMax;
                                    yInt = y01 + (y02 - y01) * (xMax - x01) / (x02 - x01);
                                }
                                else if (cfora == 4 || cfora == 5 || cfora == 6) {
                                    yInt = yMin;
                                    xInt = x01 + (x02 - x01) * (yMin - y01) / (y02 - y01);
                                }
                                else if (cfora == 8 || cfora == 9 || cfora == 10) {
                                    yInt = yMax;
                                    xInt = x01 + (x02 - x01) * (yMax - y01) / (y02 - y01);
                                }
                                if (c1 == cfora) {
                                    x01 = xInt;
                                    y01 = yInt;
                                }
                                else {
                                    x02 = xInt;
                                    y02 = yInt;
                                }
                            }
                        }
                        if (aceite) {
                            for (int j = 0; j < i.elemento.size(); j++) {
                                if (i.elemento.get(j).x < xMin) {
                                    i.elemento.remove(j);
                                }
                                else if (i.elemento.get(j).x > xMax) {
                                    i.elemento.remove(j);
                                }
                            }
                            for (int j = 0; j < i.elemento.size(); j++) {
                                if (i.elemento.get(j).y < yMin) {
                                    i.elemento.remove(j);
                                }
                                else if (i.elemento.get(j).y > yMax) {
                                    i.elemento.remove(j);
                                }
                            }
                            //colore de branco, o segmento de reta que deve ser retirado
                            g.setColor(Color.WHITE);
                            g.drawLine(i.elemento.get(0).x, i.elemento.get(0).y, x01, y01);
                            g.drawLine(x02, y02, i.elemento.get(i.elemento.size() - 1).x, i.elemento.get(i.elemento.size() - 1).y);
                            //o desenho e plotado de acordo com a cor do objeto
                            if (i.cor == 0) {
                                g.setColor(Color.RED);
                            }
                            else if (i.cor == 1) {
                                g.setColor(Color.BLACK);
                            }
                            else if (i.cor == 2) {
                                g.setColor(Color.GREEN);
                            }
                            else if (i.cor == 3) {
                                g.setColor(Color.BLUE);
                            }
                            else if (i.cor == 4) {
                                g.setColor(Color.YELLOW);
                            }
                            //plot da reta
                            g.drawLine(Math.round(x01), Math.round(y01), Math.round(x02), Math.round(y02));
                        }
                    }
                }
            }
            /*
             Caso o botao Liang-Barsky for selecionado, executaremos o if abaixo
             */
            else if (liang) {
                if (!desenhos.isEmpty()) {
                    xMin = Menor(x1, x2);
                    yMin = Menor(y1, y2);
                    xMax = Maior(x1, x2);
                    yMax = Maior(y1, y2);
                    u1 = 0.0;
                    u2 = 1.0;
                    for (Celula i = quadro.primeiro; i != null; i = i.prox) {
                        dx = i.elemento.get(i.elemento.size() - 1).x - i.elemento.get(0).x;
                        dy = i.elemento.get(i.elemento.size() - 1).y - i.elemento.get(0).y;
                        if (cliptest(-dx, i.elemento.get(0).x - xMin, u1, u2)) {
                            if (cliptest(dx, xMax - i.elemento.get(0).x, u1, u2)) {
                                if (cliptest(-dy, i.elemento.get(0).y - yMin, u1, u2)) {
                                    if (cliptest(dy, yMax - i.elemento.get(0).y, u1, u2)) {
                                        if (u2 < 1.0) {
                                            i.elemento.get(i.elemento.size() - 1).x = (int) (i.elemento.get(0).x + u2 * dx);
                                            i.elemento.get(i.elemento.size() - 1).y = (int) (i.elemento.get(0).y + u2 * dy);
                                        }
                                        if (u1 > 0.0) {
                                            i.elemento.get(0).x = (int) (i.elemento.get(0).x + u1 * dx);
                                            i.elemento.get(0).y = (int) (i.elemento.get(0).y + u1 * dy);
                                        }
                                        g.drawLine(Math.round(i.elemento.get(0).x), Math.round(i.elemento.get(0).y), Math.round(i.elemento.get(i.elemento.size() - 1).x), Math.round(i.elemento.get(i.elemento.size() - 1).y));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            /*
             Caso o botao Rasterizacao de Retas for selecionado, executaremos o if abaixo
             */
            else if (rasterizacao) {
                /*Com o intuito de evitar alocar inumeros desenhos vazios na lista,
                 verificamos se o array list desenhos encontra-se preenchido
                 e alocamos um novo elemento na lista de desenhos
                 */
                if (!desenhos.isEmpty()) {
                    ArrayList<desenho> tmp = new ArrayList<>();
                    quadro.inserir(tmp);
                    quadro.ultimo.cor = cor;
                    desenhos.clear();
                    float m = (y1 - y2) / (x1 - x2);
                    float b = y1 - (m * x1);
                    float y;
                    //o desenho e plotado de acordo com a cor desejada
                    if (cor == 0) {
                        g.setColor(Color.RED);
                    }
                    else if (cor == 1) {
                        g.setColor(Color.BLACK);
                    }
                    else if (cor == 2) {
                        g.setColor(Color.GREEN);
                    }
                    else if (cor == 3) {
                        g.setColor(Color.BLUE);
                    }
                    else if (cor == 4) {
                        g.setColor(Color.YELLOW);
                    }
                    for (int x = Menor(x1, x2); x < Maior(x1, x2); x++) {
                        y = (m * x) + b;
                        //armazena o ponto na lista de desenhos
                        quadro.ultimo.elemento.add(new desenho(x, (int) (Math.round(y))));
                        //plot do ponto
                        g.drawLine(x, (int) (Math.round(y)), x, (int) (Math.round(y)));
                    }
                }
            }
        }
    }

    /*
     Metodo usado para armazenar os pontos da circunferencia na lista de desenhos
     */
    public void inserePontos(int xc, int yc, int x, int y) {
        quadro.ultimo.elemento.add(new desenho((xc + x), (yc + y)));
        quadro.ultimo.elemento.add(new desenho((xc - x), (yc + y)));
        quadro.ultimo.elemento.add(new desenho((xc + x), (yc - y)));
        quadro.ultimo.elemento.add(new desenho((xc - x), (yc - y)));
        quadro.ultimo.elemento.add(new desenho((xc + y), (yc + x)));
        quadro.ultimo.elemento.add(new desenho((xc - y), (yc + x)));
        quadro.ultimo.elemento.add(new desenho((xc + y), (yc - x)));
        quadro.ultimo.elemento.add(new desenho((xc - y), (yc - x)));
    }
    
    public void plotCircunferencia(Graphics g, int xc, int yc, int x, int y){
        if((yc+y) >= 195){
            g.drawLine(xc + x, yc + y, xc + x, yc + y);
            g.drawLine(xc - x, yc + y, xc - x, yc + y);
        }
        if((yc-y) >= 195){
            g.drawLine(xc + x, yc - y, xc + x, yc - y);
            g.drawLine(xc - x, yc - y, xc - x, yc - y);
        }
        if((yc+x) >= 195){
            g.drawLine(xc + y, yc + x, xc + y, yc + x);
            g.drawLine(xc - y, yc + x, xc - y, yc + x);
        }
        if((yc-x) >= 195){
            g.drawLine(xc + y, yc - x, xc + y, yc - x);
            g.drawLine(xc - y, yc - x, xc - y, yc - x);
        }
    }
    
    public void plotPontos(Graphics g){
        for (int i = 0; i < k.elemento.size(); i++) {
            //o desenho e plotado de acordo com a cor do objeto
            if (k.cor == 0) {
                g.setColor(Color.RED);
            }
            else if (k.cor == 1) {
                g.setColor(Color.BLACK);
            }
            else if (k.cor == 2) {
                g.setColor(Color.GREEN);
            }
            else if (k.cor == 3) {
                g.setColor(Color.BLUE);
            }
            else if (k.cor == 4) {
                g.setColor(Color.YELLOW);
            }
            if(k.elemento.get(i).y >= 195){
                g.drawLine(k.elemento.get(i).x, k.elemento.get(i).y, k.elemento.get(i).x, k.elemento.get(i).y);
            }
        }
    }

    /*
     Metodo usado pelo Cohen-Sutherland para verificar a regiao onde se encontra um
     determinado ponto do desenho
     */
    public int regionCode(int x, int y) {
        int codigo = 0;
        if (x < xMin) {
            codigo = codigo + 1;
        }
        if (x > xMax) {
            codigo = codigo + 2;
        }
        if (y < yMin) {
            codigo = codigo + 4;
        }
        if (y > yMax) {
            codigo = codigo + 8;
        }
        return codigo;
    }

    /*
     Metodo usado para extrairmos o maior valor entre dois numeros
     */
    public int Maior(int x, int y) {
        if (x > y) {
            return x;
        }
        return y;
    }

    /*
     Metodo usado para extrairmos o menor valor entre dois numeros
     */
    public int Menor(int x, int y) {
        if (x < y) {
            return x;
        }
        return y;
    }

    /*
     Metodo usado pelo Liang-Barsky para o recorte do desenho
     */
    public boolean cliptest(int p, int q, double u1, double u2) {
        boolean result = true;
        double r;
        if (p < 0.0) {
            r = q / p;
            if (r > u2) {
                result = false;
            }
            else if (r > u1) {
                u1 = r;
            }
        }
        else if (p > 0.0) {
            r = q / p;
            if (r < u1) {
                result = false;
            }
            else if (r < u2) {
                u2 = r;
            }
        }
        else if (q < 0.0) {
            result = false;
        }
        return result;
    }
    
    public class time extends Thread{
		public void run(){
            while(true){
                /*
                 Caso o botao Salvar for selecionado, executaremos o if abaixo
                 */
                if(salvar){
                    save();
                    salvar=false;
                }
                try{
                    Point pointer = getMousePosition();
                    /*
                     Somente salvamos os pontos validos para um desenho. Ou seja, se o
                     ponto onde o usuario quer desenhar, estiver abaixo da regiao dos botoes
                     */
                    if(pointer.y >= 195){
                        setMouse(mouse);
                        //O desenho e feito apenas se o botao do mouse estiver pressionado
                        if(pressionado){
                            desenhos.add(new desenho(pointer.x,pointer.y));
                            if(desenho_livre){
                                btn_cohen.setVisible(false);
                                btn_liang.setVisible(false);
                            }
                        }
                    }
                    else{
                        setMouse(0);
                    }
                } catch(Exception erro){}
                desenhar(getGraphics());
            }
                }
        }

    /*
     Metodo usado para armazenarmos todos os pontos de todos os desenhos num arquivo
     que sera lido pelo load, toda vez que o paint for executado
     */
    public void save() {
        File file = new File("quadro.txt");
        try {
            if (!file.exists()) {
                file.delete();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            for (Celula i = quadro.primeiro; i != null; i = i.prox) {
                //A primeira linha refere-se a  cor do objeto
                bw.write(i.cor + "\n");
                for (int j = 0; j < i.elemento.size(); j++) {
                    //Armazena-se a seguir, todos os pontos do objeto 
                    bw.write(i.elemento.get(j).x + "," + i.elemento.get(j).y + " ");
                }
                bw.write("\n");
            }
            bw.close();
        } catch (Exception erro) {
        }
    }
    
    public void preencher(int x, int y, Graphics g){
        if(x < 0||x > getWidth()){
            return;
        }
        if(y < 195||y > 700){
            return;
        }
        if(image.getRGB(x,y)==colorO && image.getRGB(x,y) != colorN){
        //if(robot.getPixelColor(x,y) == oldColor){
            image.setRGB(x,y,colorN);
            update(g);
            preencher(x-1,y-1,g);
            preencher(x-1,y+1,g);
            preencher(x+1,y-1,g);
            preencher(x+1,y+1,g);
            preencher(x,y-1,g);
            preencher(x,y+1,g);
            preencher(x-1,y,g);
            preencher(x+1,y,g);
        }
    }
    
    public void Borracha(){
        try{
            File file = new File("borracha.png");
            if(!file.exists()){
                BufferedImage bufferImage =new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = bufferImage.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, 50, 50);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(0, 0, 49, 49);
                g2d.dispose();
                ImageIO.write(bufferImage,"png",file);
            }
        }
        catch(IOException e){
        }
    }
    
    /*
     Metodo usado para trocar o cursor do mouse por uma imagem. Usado para a borracha
     */
    public void MudaCursor(String nomeImagem) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image img = toolkit.getImage(nomeImagem);
        Point point = new Point(0, 0);
        Cursor cursor = toolkit.createCustomCursor(img, point, "borracha");
        setCursor(cursor);
    }

    
    /*
     Metodo onde e realizada a troca do i­cone do mouse, dependendo da opcao selecionada pelo usuario
     */
    void setMouse(int mouse) {
        Cursor cursor;
        if (mouse == 1) {
            cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
            setCursor(cursor);
            this.mouse = 1;
        }
        else if (mouse == 2) {
            cursor = new Cursor(Cursor.SW_RESIZE_CURSOR);
            setCursor(cursor);
            this.mouse = 2;
        }
        else if (mouse == 3) {
            MudaCursor("borracha.png");
            this.mouse = 3;
        }
        else {
            setCursor(null);
        }
    }
   
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btn_dda = new javax.swing.JButton();
        btn_bresenhan = new javax.swing.JButton();
        btn_circunferencia = new javax.swing.JButton();
        btn_livre = new javax.swing.JButton();
        btn_borracha = new javax.swing.JButton();
        btn_retangulos = new javax.swing.JButton();
        btn_recorte = new javax.swing.JButton();
        btn_clear = new javax.swing.JButton();
        btn_rasterizacao = new javax.swing.JButton();
        btn_cohen = new javax.swing.JButton();
        btn_liang = new javax.swing.JButton();
        btn_translacao = new javax.swing.JButton();
        btn_rotacao = new javax.swing.JButton();
        btn_escala = new javax.swing.JButton();
        btn_reflexao = new javax.swing.JButton();
        btn_vermelho = new javax.swing.JButton();
        btn_cima = new javax.swing.JButton();
        btn_baixo = new javax.swing.JButton();
        btn_esquerda = new javax.swing.JButton();
        btn_direita = new javax.swing.JButton();
        btn_rotacionar = new javax.swing.JButton();
        btn_mais = new javax.swing.JButton();
        btn_menos = new javax.swing.JButton();
        btn_okTransformacao = new javax.swing.JButton();
        btn_salvar = new javax.swing.JButton();
        btn_preto = new javax.swing.JButton();
        btn_verde = new javax.swing.JButton();
        btn_azul = new javax.swing.JButton();
        btn_amarelo = new javax.swing.JButton();
        btn_interpoladas = new javax.swing.JButton();
        btn_hermite = new javax.swing.JButton();
        btn_bezier = new javax.swing.JButton();
        btn_okBezier = new javax.swing.JButton();
        btn_preenchimento = new javax.swing.JButton();

        jButton3.setText("jButton3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        btn_dda.setText("DDA");
        btn_dda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ddaActionPerformed(evt);
            }
        });

        btn_bresenhan.setText("Bresenhan");
        btn_bresenhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_bresenhanActionPerformed(evt);
            }
        });

        btn_circunferencia.setText("Circunferencia");
        btn_circunferencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_circunferenciaActionPerformed(evt);
            }
        });

        btn_livre.setText("Desenho Livre");
        btn_livre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_livreActionPerformed(evt);
            }
        });

        btn_borracha.setText("Borracha");
        btn_borracha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_borrachaActionPerformed(evt);
            }
        });

        btn_retangulos.setText("Retangulos");
        btn_retangulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_retangulosActionPerformed(evt);
            }
        });

        btn_recorte.setText("Recorte Trivial");
        btn_recorte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recorteActionPerformed(evt);
            }
        });

        btn_clear.setText("Limpar Quadro");
        btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearActionPerformed(evt);
            }
        });

        btn_rasterizacao.setText("Rasterizacao de Retas");
        btn_rasterizacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_rasterizacaoActionPerformed(evt);
            }
        });

        btn_cohen.setText("Cohen-Sutherland");
        btn_cohen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cohenActionPerformed(evt);
            }
        });

        btn_liang.setText("Liang-Barsky");
        btn_liang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_liangActionPerformed(evt);
            }
        });

        btn_translacao.setText("Translacao");
        btn_translacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_translacaoActionPerformed(evt);
            }
        });

        btn_rotacao.setText("Rotacao");
        btn_rotacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_rotacaoActionPerformed(evt);
            }
        });

        btn_escala.setText("Escala");
        btn_escala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_escalaActionPerformed(evt);
            }
        });

        btn_reflexao.setText("Reflexao");
        btn_reflexao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reflexaoActionPerformed(evt);
            }
        });

        btn_vermelho.setBackground(new java.awt.Color(255, 0, 0));
        btn_vermelho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_vermelhoActionPerformed(evt);
            }
        });

        btn_cima.setText("Cima");
        btn_cima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cimaActionPerformed(evt);
            }
        });

        btn_baixo.setText("Baixo");
        btn_baixo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_baixoActionPerformed(evt);
            }
        });

        btn_esquerda.setText("Esquerda");
        btn_esquerda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_esquerdaActionPerformed(evt);
            }
        });

        btn_direita.setText("Direita");
        btn_direita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_direitaActionPerformed(evt);
            }
        });

        btn_rotacionar.setText("Rotacionar");
        btn_rotacionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_rotacionarActionPerformed(evt);
            }
        });

        btn_mais.setText("Mais");
        btn_mais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_maisActionPerformed(evt);
            }
        });

        btn_menos.setText("Menos");
        btn_menos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_menosActionPerformed(evt);
            }
        });

        btn_okTransformacao.setText("OK");
        btn_okTransformacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_okTransformacaoActionPerformed(evt);
            }
        });

        btn_salvar.setText("Salvar");
        btn_salvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salvarActionPerformed(evt);
            }
        });

        btn_preto.setBackground(new java.awt.Color(0, 0, 0));
        btn_preto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_pretoActionPerformed(evt);
            }
        });

        btn_verde.setBackground(new java.awt.Color(0, 255, 0));
        btn_verde.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verdeActionPerformed(evt);
            }
        });

        btn_azul.setBackground(new java.awt.Color(0, 0, 255));
        btn_azul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_azulActionPerformed(evt);
            }
        });

        btn_amarelo.setBackground(new java.awt.Color(255, 255, 0));
        btn_amarelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_amareloActionPerformed(evt);
            }
        });

        btn_interpoladas.setText("Curvas Interpoladas");
        btn_interpoladas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_interpoladasActionPerformed(evt);
            }
        });

        btn_hermite.setText("Curvas de Hermite");
        btn_hermite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hermiteActionPerformed(evt);
            }
        });

        btn_bezier.setText("Curvas de Bezier");
        btn_bezier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_bezierActionPerformed(evt);
            }
        });

        btn_okBezier.setText("Plot Bezier");
        btn_okBezier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_okBezierActionPerformed(evt);
            }
        });

        btn_preenchimento.setText("Preenchimento");
        btn_preenchimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_preenchimentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btn_vermelho, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_preto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_verde, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_azul, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_amarelo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_rasterizacao))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(btn_cohen)
                                .addGap(18, 18, 18)
                                .addComponent(btn_liang)
                                .addGap(18, 18, 18)
                                .addComponent(btn_translacao)
                                .addGap(18, 18, 18)
                                .addComponent(btn_rotacao)
                                .addGap(18, 18, 18)
                                .addComponent(btn_escala)
                                .addGap(18, 18, 18)
                                .addComponent(btn_reflexao))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_interpoladas)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_hermite)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_bezier)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_preenchimento)))
                        .addContainerGap(61, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btn_dda)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_bresenhan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_circunferencia)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_livre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_borracha)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_retangulos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_recorte)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_clear))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btn_cima)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_baixo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_esquerda)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_direita)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_rotacionar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_mais)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_menos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_okTransformacao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_okBezier)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_salvar)))
                        .addGap(0, 40, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_dda)
                    .addComponent(btn_bresenhan)
                    .addComponent(btn_circunferencia)
                    .addComponent(btn_livre)
                    .addComponent(btn_borracha)
                    .addComponent(btn_retangulos)
                    .addComponent(btn_recorte)
                    .addComponent(btn_clear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_rasterizacao)
                    .addComponent(btn_cohen)
                    .addComponent(btn_liang)
                    .addComponent(btn_translacao)
                    .addComponent(btn_rotacao)
                    .addComponent(btn_escala)
                    .addComponent(btn_reflexao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_vermelho, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_preto, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_verde, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_azul, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_amarelo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_interpoladas)
                        .addComponent(btn_hermite)
                        .addComponent(btn_bezier)
                        .addComponent(btn_preenchimento)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_cima)
                    .addComponent(btn_baixo)
                    .addComponent(btn_esquerda)
                    .addComponent(btn_direita)
                    .addComponent(btn_rotacionar)
                    .addComponent(btn_mais)
                    .addComponent(btn_menos)
                    .addComponent(btn_okTransformacao)
                    .addComponent(btn_okBezier)
                    .addComponent(btn_salvar))
                .addContainerGap(302, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ddaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ddaActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = true;
        desenho_livre = false;
        borracha = false;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        preenchimento = false;
        hermite = false;
        interpolacao = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_ddaActionPerformed

    private void btn_bresenhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_bresenhanActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = true;
        desenho_livre = false;
        borracha = false;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        preenchimento = false;
        hermite = false;
        interpolacao = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_bresenhanActionPerformed

    private void btn_circunferenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_circunferenciaActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = true;
        desenho_livre = false;
        borracha = false;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        preenchimento = false;
        hermite = false;
        interpolacao = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_circunferenciaActionPerformed

    private void btn_livreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_livreActionPerformed
        load = false;
        desenhos.clear();
        desenho_livre = true;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        preenchimento = false;
        hermite = false;
        interpolacao = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(2);
    }//GEN-LAST:event_btn_livreActionPerformed

    private void btn_borrachaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_borrachaActionPerformed
        load = false;
        desenhos.clear();
        desenho_livre = false;
        borracha = true;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        preenchimento = false;
        hermite = false;
        interpolacao = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(3);
    }//GEN-LAST:event_btn_borrachaActionPerformed

    private void btn_retangulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_retangulosActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = true;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        preenchimento = false;
        hermite = false;
        interpolacao = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_retangulosActionPerformed

    private void btn_recorteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recorteActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = true;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        preenchimento = false;
        hermite = false;
        interpolacao = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_recorteActionPerformed

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearActionPerformed
        load = false;
        desenhos.clear();
        k = null;
        clear = true;
        translacao = false;
        rotacao = false;
        escala = false;
        bezier = false;
        reflexao = false;
        preenchimento = false;
        hermite = false;
        interpolacao = false;
        btn_okBezier.setVisible(false);
        btn_cohen.setVisible(true);
        btn_liang.setVisible(true);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
    }//GEN-LAST:event_btn_clearActionPerformed

    private void btn_cohenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cohenActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = true;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        hermite = false;
        interpolacao = false;
        preenchimento = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_cohenActionPerformed

    private void btn_liangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_liangActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = true;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        hermite = false;
        interpolacao = false;
        preenchimento = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_liangActionPerformed

    private void btn_rasterizacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_rasterizacaoActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = false;
        rasterizacao = true;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        hermite = false;
        interpolacao = false;
        preenchimento = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_rasterizacaoActionPerformed

    private void btn_translacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_translacaoActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = false;
        rasterizacao = false;
        translacao = true;
        rotacao = false;
        escala = false;
        reflexao = false;
        cima = false;
        baixo = false;
        esquerda = false;
        direita = false;
        k = null;
        bezier = false;
        hermite = false;
        interpolacao = false;
        preenchimento = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(true);
        btn_baixo.setVisible(true);
        btn_esquerda.setVisible(true);
        btn_direita.setVisible(true);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(true);
        setMouse(1);
    }//GEN-LAST:event_btn_translacaoActionPerformed

    private void btn_okTransformacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_okTransformacaoActionPerformed
        translacao = false;
        rotacao = false;
        reflexao = false;
        recorte = false;
        escala = false;
        k = null;
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
    }//GEN-LAST:event_btn_okTransformacaoActionPerformed

    private void btn_rotacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_rotacaoActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = false;
        rasterizacao = false;
        translacao = false;
        rotacao = true;
        escala = false;
        reflexao = false;
        k = null;
        bezier = false;
        preenchimento = false;
        interpolacao = false;
        hermite = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(true);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(true);
        setMouse(1);
    }//GEN-LAST:event_btn_rotacaoActionPerformed

    private void btn_reflexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reflexaoActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = false;
        rasterizacao = false;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = true;
        k = null;
        bezier = false;
        hermite = false;
        interpolacao = false;
        preenchimento = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(true);
        setMouse(1);
    }//GEN-LAST:event_btn_reflexaoActionPerformed

    private void btn_escalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_escalaActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = false;
        rasterizacao = false;
        translacao = false;
        rotacao = false;
        escala = true;
        reflexao = false;
        mais = false;
        menos = false;
        bezier = false;
        hermite = false;
        interpolacao = false;
        k = null;
        preenchimento = false;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(true);
        btn_menos.setVisible(true);
        btn_okTransformacao.setVisible(true);
        setMouse(1);
    }//GEN-LAST:event_btn_escalaActionPerformed

    private void btn_cimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cimaActionPerformed
        cima = true;
    }//GEN-LAST:event_btn_cimaActionPerformed

    private void btn_baixoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_baixoActionPerformed
        baixo = true;
    }//GEN-LAST:event_btn_baixoActionPerformed

    private void btn_esquerdaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_esquerdaActionPerformed
        esquerda = true;
    }//GEN-LAST:event_btn_esquerdaActionPerformed

    private void btn_direitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_direitaActionPerformed
        direita = true;
    }//GEN-LAST:event_btn_direitaActionPerformed

    private void btn_rotacionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_rotacionarActionPerformed
        rotacionar = true;
    }//GEN-LAST:event_btn_rotacionarActionPerformed

    private void btn_maisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_maisActionPerformed
        mais = true;
    }//GEN-LAST:event_btn_maisActionPerformed

    private void btn_menosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_menosActionPerformed
        menos = true;
    }//GEN-LAST:event_btn_menosActionPerformed

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed
        load = false;
        salvar = true;
    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_vermelhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_vermelhoActionPerformed
        load = false;
        cor = 0;
    }//GEN-LAST:event_btn_vermelhoActionPerformed

    private void btn_pretoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_pretoActionPerformed
        load = false;
        cor = 1;
    }//GEN-LAST:event_btn_pretoActionPerformed

    private void btn_verdeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verdeActionPerformed
        load = false;
        cor = 2;
    }//GEN-LAST:event_btn_verdeActionPerformed

    private void btn_azulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_azulActionPerformed
        load = false;
        cor = 3;
    }//GEN-LAST:event_btn_azulActionPerformed

    private void btn_amareloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_amareloActionPerformed
        load = false;
        cor = 4;
    }//GEN-LAST:event_btn_amareloActionPerformed

    private void btn_bezierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_bezierActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = false;
        rasterizacao = false;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        bezier = true;
        preenchimento = false;
        hermite = false;
        interpolacao = false;
        k = null;
        contPontos = 0;
        btn_okBezier.setVisible(true);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_bezierActionPerformed

    private void btn_okBezierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_okBezierActionPerformed
        plotBezier = true;
    }//GEN-LAST:event_btn_okBezierActionPerformed

    private void btn_preenchimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_preenchimentoActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = false;
        rasterizacao = false;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        bezier = false;
        preenchimento = true;
        hermite = false;
        interpolacao = false;
        k = null;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_preenchimentoActionPerformed

    private void btn_hermiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hermiteActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = false;
        rasterizacao = false;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        bezier = false;
        preenchimento = true;
        hermite = true;
        interpolacao = false;
        k = null;
        contPontos = 0;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_hermiteActionPerformed

    private void btn_interpoladasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_interpoladasActionPerformed
        load = false;
        desenhos.clear();
        dda_abilitado = false;
        bresenhan_abilitado = false;
        circunferencia_abilitado = false;
        desenho_livre = false;
        borracha = false;
        retangulo = false;
        recorte = false;
        clear = false;
        cohen = false;
        liang = false;
        rasterizacao = false;
        translacao = false;
        rotacao = false;
        escala = false;
        reflexao = false;
        bezier = false;
        preenchimento = true;
        hermite = false;
        interpolacao = true;
        k = null;
        contPontos = 0;
        btn_okBezier.setVisible(false);
        btn_cima.setVisible(false);
        btn_baixo.setVisible(false);
        btn_esquerda.setVisible(false);
        btn_direita.setVisible(false);
        btn_rotacionar.setVisible(false);
        btn_mais.setVisible(false);
        btn_menos.setVisible(false);
        btn_okTransformacao.setVisible(false);
        setMouse(1);
    }//GEN-LAST:event_btn_interpoladasActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CGPaint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CGPaint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CGPaint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CGPaint.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CGPaint().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_amarelo;
    private javax.swing.JButton btn_azul;
    private javax.swing.JButton btn_baixo;
    private javax.swing.JButton btn_bezier;
    private javax.swing.JButton btn_borracha;
    private javax.swing.JButton btn_bresenhan;
    private javax.swing.JButton btn_cima;
    private javax.swing.JButton btn_circunferencia;
    private javax.swing.JButton btn_clear;
    private javax.swing.JButton btn_cohen;
    private javax.swing.JButton btn_dda;
    private javax.swing.JButton btn_direita;
    private javax.swing.JButton btn_escala;
    private javax.swing.JButton btn_esquerda;
    private javax.swing.JButton btn_hermite;
    private javax.swing.JButton btn_interpoladas;
    private javax.swing.JButton btn_liang;
    private javax.swing.JButton btn_livre;
    private javax.swing.JButton btn_mais;
    private javax.swing.JButton btn_menos;
    private javax.swing.JButton btn_okBezier;
    private javax.swing.JButton btn_okTransformacao;
    private javax.swing.JButton btn_preenchimento;
    private javax.swing.JButton btn_preto;
    private javax.swing.JButton btn_rasterizacao;
    private javax.swing.JButton btn_recorte;
    private javax.swing.JButton btn_reflexao;
    private javax.swing.JButton btn_retangulos;
    private javax.swing.JButton btn_rotacao;
    private javax.swing.JButton btn_rotacionar;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JButton btn_translacao;
    private javax.swing.JButton btn_verde;
    private javax.swing.JButton btn_vermelho;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables
    Lista quadro=new Lista();
    int mouse=0,xMin,xMax,yMin,yMax,xInicio=0,xFim=0,yInicio=0,yFim=0,x01,y01,x02,y02,cor=1,contPontos=0, colorN, colorO;
    Celula k=null;
    desenho pontos[] = new desenho[4], pontosHermite[] = new desenho[3];
    BufferedImage image;
    Color oldColor,newColor;
    double matrizInterpolacao[][] = new double[4][4];
    Robot robot;
    double u1=0.0,u2=1.0;
    boolean salvar=false,load=true,reflexao=false,escala=false,refletir=false,mais=false,menos=false, bezier = false, plotBezier = false, hermite = false, novoDesenhoLivre = false;
    boolean cima=false,baixo=false,esquerda=false,direita=false,rotacionar=false, preenchimento=false, interpolacao = false;
    boolean pressionado=false,dda_abilitado=false,bresenhan_abilitado=false,circunferencia_abilitado=false,desenho_livre=true, borracha=false,clear=false,retangulo=false,recorte=false,cohen=false, liang=false,rasterizacao=false,posicaoRecorte=false,translacao=false,click=false,rotacao=false;
    ArrayList<desenho> desenhos = new ArrayList<>();

}
