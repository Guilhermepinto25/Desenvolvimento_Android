package com.mygame.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Random;

import sun.rmi.runtime.Log;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;

    private int estadoJogo = 0;

    private Circle passaroCirculo;
    private Rectangle canoTopoRetangulo1;
    private Rectangle canoBaixoRetangulo1;
    private Rectangle canoTopoRetangulo2;
    private Rectangle canoBaixoRetangulo2;
//    private ShapeRenderer shape;

    //Atributos de configuracao
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int pontuacao = 0;

    private Preferences prefs;
    private int bestScore;

    private float deltaTime;
    private float variacao = 0;
	private float velocidadeQueda = 0;

	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal1;
	private float posicaoMovimentoCanoHorizontal2;
    private float espacoEntreCanos;
    private float alturaEntreCanosRandomica1;
    private float alturaEntreCanosRandomica2;
    private Random numeroRandomico;

	private float posicaoPassaroX;

	private BitmapFont mensagemReiniciar;
    private BitmapFont placar;
    private BitmapFont bestScoreAtual;
	private boolean marcouPonto = false;

	//Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HIGH = 1024;


	@Override
	public void create () {
        batch = new SpriteBatch();
        numeroRandomico = new Random();
        passaroCirculo = new Circle();
        /*canoBaixoRetangulo = new Rectangle();
        canoTopoRetangulo = new Rectangle();
        shape = new ShapeRenderer();*/

        placar = new BitmapFont(
                Gdx.files.internal("font.fnt"),
                Gdx.files.internal("font.png"),
                false);
        //placar.setColor(Color.WHITE);
        placar.getData().setScale(1);

        mensagemReiniciar = new BitmapFont(
                Gdx.files.internal("font.fnt"),
                Gdx.files.internal("font.png"),
                false);
        mensagemReiniciar.setColor(Color.WHITE);
        mensagemReiniciar.getData().setScale((float) 0.25);

        prefs = Gdx.app.getPreferences("My Preferences");
        bestScore =  prefs.getInteger("bestScore",0);

        bestScoreAtual = new BitmapFont(
                Gdx.files.internal("font.fnt"),
                Gdx.files.internal("font.png"),
                false);
        //bestScoreAtual.setColor(Color.WHITE);
        bestScoreAtual.getData().setScale((float) 0.2);

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");

        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");

        gameOver = new Texture("game_over.png");

        /***** Configuracoes da Camera *****/
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HIGH/2,0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HIGH, camera);

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HIGH;

        posicaoInicialVertical = alturaDispositivo/2;
        posicaoMovimentoCanoHorizontal1 = larguraDispositivo;
        posicaoMovimentoCanoHorizontal2 = larguraDispositivo + 400; //400 é a distancia entre dois canos consecutivos
        posicaoPassaroX = larguraDispositivo/2 - passaros[0].getWidth()/2;
        espacoEntreCanos = 500;
}

	@Override
	public void render () {
	    camera.update();

	    //Limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();

        variacao += deltaTime * 10;
        if (variacao >= 3) variacao = 0;

        if(estadoJogo == 0){ //Nao iniciado
            if (Gdx.input.justTouched()){
                estadoJogo = 1;
            }
        }else { //Iniciado
            velocidadeQueda ++;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0) posicaoInicialVertical -= velocidadeQueda;

            if(estadoJogo == 1){
                posicaoMovimentoCanoHorizontal1 -= deltaTime * 250;
                posicaoMovimentoCanoHorizontal2 -= deltaTime * 250;

                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -15;
                }
                //Verifica se o cano 1 saiu inteiramente da tela
                if (posicaoMovimentoCanoHorizontal1 < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal1 = larguraDispositivo;
                    alturaEntreCanosRandomica1 = numeroRandomico.nextInt(500) - 250;
                    marcouPonto = false;
                }
                //Verifica se o cano 2 saiu inteiramente da tela
                if (posicaoMovimentoCanoHorizontal2 < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal2 = larguraDispositivo;
                    alturaEntreCanosRandomica2 = numeroRandomico.nextInt(500) - 250;
                    marcouPonto = false;
                }

                //Verifica pontuacao
                if (posicaoMovimentoCanoHorizontal1 < posicaoPassaroX || posicaoMovimentoCanoHorizontal2 < posicaoPassaroX) {
                    if (!marcouPonto) {
                        marcouPonto = true;
                        pontuacao++;
                    }
                }
            }else { //Game Over - estadoJogo = 2
                if (Gdx.input.justTouched()){
                    if (pontuacao > bestScore ){
                        prefs.putInteger("bestScore", pontuacao);
                        prefs.flush();
                    }
                    estadoJogo = 0;
                    pontuacao = 0;
                    marcouPonto = false;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo/2;
                    posicaoMovimentoCanoHorizontal1 = larguraDispositivo;
                    posicaoMovimentoCanoHorizontal2 = larguraDispositivo + 400;
                }
            }
        }


        //Movimentacao Inicial
        /*if (start == false){
            if(posicaoInicialVertical > alturaDispositivo/2 - 100){
                posicaoInicialVertical -= velocidadeQueda;
            }else if (posicaoInicialVertical < alturaDispositivo/2 + 100){
                posicaoInicialVertical += velocidadeQueda;
            }
        }*/

        //Configurar dados de projecao da camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(fundo, 0,0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal1,alturaDispositivo - canoTopo.getHeight() + espacoEntreCanos/2 + alturaEntreCanosRandomica1);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal1, -espacoEntreCanos/2 + alturaEntreCanosRandomica1);
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal2,alturaDispositivo - canoTopo.getHeight() + espacoEntreCanos/2 + alturaEntreCanosRandomica2);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal2, -espacoEntreCanos/2 + alturaEntreCanosRandomica2);

        batch.draw(passaros[(int)variacao],posicaoPassaroX, posicaoInicialVertical);
        placar.draw(batch,
                String.valueOf(pontuacao),
                larguraDispositivo/2 - placar.getScaleX() - ((pontuacao<10) ? 30:50),
                alturaDispositivo - 100 );
        bestScoreAtual.draw(batch, "Best Score: " + String.valueOf(prefs.getInteger("bestScore",0)), 20, alturaDispositivo - 20 );

        if (estadoJogo == 2){
            batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
            mensagemReiniciar.draw(batch, "Toque para Reiniciar!",larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2 - 50 );
        }

        batch.end();

        passaroCirculo.set(posicaoPassaroX + passaros[0].getWidth()/2, posicaoInicialVertical + passaros[0].getHeight()/2, passaros[0].getWidth()/2 );

        canoBaixoRetangulo1 = new Rectangle(
                posicaoMovimentoCanoHorizontal1,
                -espacoEntreCanos/2 + alturaEntreCanosRandomica1,
                canoBaixo.getWidth(),
                canoBaixo.getHeight()
        );

        canoTopoRetangulo1 = new Rectangle(
                posicaoMovimentoCanoHorizontal1,
                alturaDispositivo - canoTopo.getHeight() + espacoEntreCanos/2 + alturaEntreCanosRandomica1,
                canoTopo.getWidth(),
                canoTopo.getHeight()
        );

        canoBaixoRetangulo2 = new Rectangle(
                posicaoMovimentoCanoHorizontal2,
                -espacoEntreCanos/2 + alturaEntreCanosRandomica2,
                canoBaixo.getWidth(),
                canoBaixo.getHeight()
        );

        canoTopoRetangulo2 = new Rectangle(
                posicaoMovimentoCanoHorizontal2,
                alturaDispositivo - canoTopo.getHeight() + espacoEntreCanos/2 + alturaEntreCanosRandomica2,
                canoTopo.getWidth(),
                canoTopo.getHeight()
        );

        //Desenhar formas
        /*shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shape.rect(canoBaixoRetangulo.x, canoBaixoRetangulo.y, canoBaixoRetangulo.width, canoBaixoRetangulo.height);
        shape.rect(canoTopoRetangulo.x, canoTopoRetangulo.y, canoTopoRetangulo.width, canoTopoRetangulo.height);
        shape.setColor(Color.RED);
        shape.end();*/

        //Verifica Colisao
        if (    Intersector.overlaps(passaroCirculo, canoBaixoRetangulo1) ||
                Intersector.overlaps(passaroCirculo, canoTopoRetangulo1) ||
                Intersector.overlaps(passaroCirculo, canoBaixoRetangulo2) ||
                Intersector.overlaps(passaroCirculo, canoTopoRetangulo2) ||
                posicaoInicialVertical <= 0){
            estadoJogo = 2;
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
