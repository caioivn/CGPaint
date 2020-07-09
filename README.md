Este projeto contém as funções do primeiro trabalho (Paint Brush) com o acréscimo das funções do segundo trabalho (Ferramenta de desenho gráfico)

O projeto necessita que os seguintes arquivos sejam compilados: desenho.java Celula.java Lista.java CGPaint.java

Para executar o projeto, basta apenas executar o arquivo: CGpaint.java

Por questão de simplificação, criamos o arquivo BuildAndRun.sh com os seguintes comandos: javac desenho.java Celula.java Lista.java CGPaint.java java CGPaint

Pode ser que o arquivo BildAndRun.sh apresente problemas de permissão, sugerimos que o usuário execute o seguinte comando: chmod +x BuildAndRun.sh

Para executar o projeto, basta executar o seguinte comando: ./BuildAndRun.sh

Este projeto é uma aplicação de desenho gráfico composta por inúmeras ferramentas, sendo elas:

DDA: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao DDA, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o usuário arrasta o mouse até uma posição desejada na área de desenho, e solta o botão do mouse para que seja realizado o plot da reta.

Bresenham: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Bresenham, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o usuário arrasta o mouse até uma posição desejada na área de desenho, e solta o botão do mouse para que seja realizado o plot da reta.

Desenho livre: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Desenho Livre, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o programa fará o plot dos pontos. E na medida em que o usuário movimenta o mouse, os pontos são plotados.

Circunferência: Para usar esta ferramenta, o usuário após clicar no botão correspondente à Circunferencia, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o usuário arrasta o mouse até uma posição desejada na área de desenho, e solta o botão do mouse para que seja realizado o plot da circunferência.

Retângulos: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Retangulos, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o usuário arrasta o mouse até uma posição desejada na área de desenho, e solta o botão do mouse para que seja realizado o plot do retângulo.

Borracha: Para usar esta ferramenta, o usuário após clicar no botão correspondente à Borracha, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o programa apagará os pontos que foram plotados. E na medida em que o usuário movimenta o mouse, os pontos são apagados. Neste método, por questão de simplificação, quando o usuário seleciona esta ferramenta, o programa perde as referências de todos os desenhos presentes no quadro de desenhos. Isto ocorre pois, existe a possibilidade do usuário apagar todos os desenhos presentes no quadro. E como a complexidade do projeto seria muito maior se retirássemos as referências correspondentes a cada ponto separadamente, pois o usuário poderia fragmentar um desenho em inúmeras partes. E como cada fragmento teria de ser tratado como um desenho diferente, resolvemos tomar esta medida apenas por motivos de simplificação. A ferramenta de borracha utiliza uma imagem gerada pelo próprio programa na qual corresponde em um quadrado em branco para subtituir o cursor do mouse. Com o intuito de ficar visualmente mais adequado.

Limpar Quadro: Para usar esta ferramenta, basta que o usuário clique no botão correspondente à Limpar Quadro para que o quadro de desenho seja completamente apagado.

Salvar: Quando o programa começa, a primeira coisa que ele tenta fazer é recuperar os desenhos que foram feitos anteriormente e salvos. Caso haja algum quadro de desenho salvo, os desenhos com suas respectivas cores serão recuperados. Caso o usuário queira salvar seu quadro de desenho, basta que o mesmo clique no botão Salvar. Ao fazer isso, o programa irá salvar num arquivo chamado: quadro.txt uma informação para cada desenho indicando sua cor, além disso, o programa salva todas as posições correspondentes aos pontos de cada desenho plotado.

Cohen-Sutherland: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Cohen-Sutherland, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o usuário arrasta o mouse até uma posição desejada na área de desenho, e solta o botão do mouse para que seja realizado o recorte. Todos os pontos fora da região selecionada são apagados.

Liang-Barsky: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Liang-Barsky, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o usuário arrasta o mouse até uma posição desejada na área de desenho, e solta o botão do mouse para que seja realizado o recorte. Todos os pontos fora da região selecionada são apagados.

Recorte Trivial: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Recorte Trivial, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o usuário arrasta o mouse até uma posição desejada na área de desenho, e solta o botão do mouse para que seja realizado o recorte. Todos os pontos fora da região selecionada são apagados. Este método foi implementado apenas porque nos métodos Cohen-Sutherland e Liang-Barsky, eles não são capazes de recortar um Desenho Livre. Pois, para que isso fosse possível, seria necessário armazenar os pontos deste tipo de desenho. Os pontos não são armazenados, pois na hora de realizar o reconhecimento dos pontos, como estes são plotados em tempo real, o programa acaba não conseguindo plotar todos os pontos por onde o usuário passou. Devido a isso, resolvemos este problema traçando um retar do último ponto plotado até o próximo que foi reconhecido. Porém, não conseguimos reconhecer e armazenar os pontos intermediários. Devido a isso, caso não tomássemos essa medida, haveria uma perda de qualidade neste tipo de desenho. E por este motivo, quando o botão que corresponde ao Desenho Livre é selecionado e o botão do mouse é pressionado dentro da região de desenho, os botões de recorte: Cohen-Sutherland e Liang-Barsky são desativados por medida de evitar possíveis bugs no programa.

Cores: O programa é composto por 5 botões de cores para que o usuário faça seus desenhos. Neste programa há as seguintes cores: vermelho, preto, azul, verde e amarelo. Ao selecionar uma das cores, todos os próximos desenhos serão plotados na cor desejada.

Rasterização de retas: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Rasterizacao, ele pressiona o botão esquedo do mouse dentro da área de desenho (dentro dos limites da janela e abaixo da região reservada para os botões). Ao pressionar o botão esquerdo do mouse, o usuário arrasta o mouse até uma posição desejada na área de desenho, e solta o botão do mouse para que seja realizado o plot da reta. Este método contém o algoritmo mais básico no que se refere à plotagem de uma reta (primeiro algoritmo que foi demonstrado em sala de aula).

Translação: Para usar esta ferramenta, o usuário após clicar no botão correspondente à Translação, ele seleciona um desenho o qual deseja transladar, clicando sobre ele. Ao fazer isso, o usuário poderá utilizar os botões: cima, baixo, esquerda e direita para movimentar o desenho desejado. Além do botão Ok, servindo apenas para quando o usuário terminar de transladar o desenho desejado. Os desenhos sempre andarão num passo de 5 em 5 pixels.

Reflexão: Para usar esta ferramenta, o usuário após clicar no botão correspondente à Reflexão, ele seleciona um desenho o qual deseja refletir, clicando sobre ele. Além do botão Ok, servindo apenas para quando o usuário terminar de refletir o desenho desejado.

Rotação: Para usar esta ferramenta, o usuário após clicar no botão correspondente à Rotação, ele seleciona um desenho o qual deseja rotacionar, clicando sobre ele. Ao fazer isso, o usuário poderá utilizar o botão: rotacionar para rotacionar o desenho desejado. Além do botão Ok, servindo apenas para quando o usuário terminar de rotacionar o desenho desejado.

Escala: Para usar esta ferramenta, o usuário após clicar no botão correspondente à Escala, ele seleciona um desenho o qual deseja alterar seu tamanho, clicando sobre ele. Ao fazer isso, o usuário poderá utilizar os botões: mais e menos para respectivamente aumentar ou diminuir o tamanho desenho desejado. Além do botão Ok, servindo apenas para quando o usuário terminar de alterar o tamanho do desenho desejado.

Preenchimento: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Preenchimento, ele seleciona um desenho o qual deseja preencher. Este método está apresentando um problema no momento em que ele não está sendo capaz de identificar corretaemnte a cor antiga de um pixel qualquer, para que lhe seja atribuída uma nova cor desejada.

Curvas Interpoladas: Para usar esta ferramenta, o usuário após clicar no botão correspondente à Interpolação, ele clica 4 vezes no quadro de desenho indicando os pontos de controle da curva, para que seja realizado o plot da curva interpolada. Este método segue uma implementação do método vista na aula correspondente à curva interpolada, mas infelizmente não está funcionando corretamente.

Curvas de Hermite: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Hermite, ele clica 3 vezes no quadro de desenho indicando os pontos de controle da curva, para que seja realizado o plot da curva de Hermite.

Curvas de Bezier: Para usar esta ferramenta, o usuário após clicar no botão correspondente ao Bezier, ele pode clicar 2 ou 3 ou 4 vezes no quadro de desenho indicando os pontos de controle da curva, para que seja realizado o plot da curva de Bezier. Após clicar a quantidade de pontos de controle desejados (2 ou 3 ou 4), o usuário pode solicitar que o programa realize o plot da curva de Bezier quando clicar no botão: Plot Bezier.
