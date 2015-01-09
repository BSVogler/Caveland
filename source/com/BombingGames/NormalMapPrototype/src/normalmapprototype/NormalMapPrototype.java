package normalmapprototype;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
 
/**
 * LibGDX port of ShaderLesson6, i.e. normal mapping in 2D games.
 * @author davedes
 */
public class NormalMapPrototype implements ApplicationListener {
  
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.width = 1920;
		cfg.height = 1080;
		cfg.resizable = false;
		LwjglApplication instance = new LwjglApplication(new NormalMapPrototype(), cfg);
	}
	
	private Texture diffTexture, normalTexture;
	private TextureAtlas diffuseTextureRegion;
	
	private ShapeRenderer shR;
	private AtlasRegion[][] texture;
	
	private SpriteBatch batch;
	private OrthographicCamera cam;
	
	private ShaderProgram shader;
 
	//our constants...
	public static final float DEFAULT_LIGHT_Z = 0.075f;
	public static final float AMBIENT_INTENSITY = 0.2f;
	public static final float LIGHT_INTENSITY = 1f;
	
	public static final Vector3 LIGHT_NORMAL = new Vector3(0f,0f,DEFAULT_LIGHT_Z);
	
	//Light RGB and intensity (alpha)
	public static final Vector3 LIGHT_COLOR = new Vector3(1f, 0.8f, 0.6f);
 
	//Ambient RGB and intensity (alpha)
	public static final Vector3 AMBIENT_COLOR = new Vector3(0.6f, 0.6f, 1f);
	private BitmapFont font;
	
	@Override
	public void create() {
		diffuseTextureRegion = new TextureAtlas(Gdx.files.internal("normalmapprototype/Spritesheet.txt"));
		diffTexture = diffuseTextureRegion.getTextures().first();
		normalTexture = new Texture(Gdx.files.internal("normalmapprototype/SpritesheetNormal.png"));
		
		ShaderProgram.pedantic = false;
		String vertexShader = Gdx.files.internal("normalmapprototype/vertexNM.vs").readString();
        String fragmentShader = Gdx.files.internal("normalmapprototype/fragmentNM.fs").readString();
        shader = new ShaderProgram(vertexShader, fragmentShader);
		
		//ensure it compiled
		if (!shader.isCompiled())
			throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());
		//print any warnings
		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());
		
		//setup default uniforms
		shader.begin();
 
		//our normal map
		shader.setUniformi("u_normals", 1); //GL_TEXTURE1
		
		//light/ambient colors
		//LibGDX doesn't have Vector4 class at the moment, so we pass them individually...
		shader.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
		shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
		
		//LibGDX likes us to end the shader program
		shader.end();
		
		batch = new SpriteBatch(200, shader);
		batch.setShader(shader);
		
		shR = new ShapeRenderer(2);
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.setToOrtho(false);
		
		//handle mouse wheel
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean scrolled(int delta) {
				//LibGDX mouse wheel is inverted compared to lwjgl-basics
				LIGHT_NORMAL.z = Math.max(0f, LIGHT_NORMAL.z - (delta * 0.005f));
				System.out.println("New light Z: "+LIGHT_NORMAL.z);
				return true;
			}
		});
		
		//indexTextures
		texture = new AtlasRegion[5][3];
		for (int i = 0; i < 5; i++) {
			texture[i][0] = diffuseTextureRegion.findRegion("b"+i+"-0-0");
			texture[i][1] = diffuseTextureRegion.findRegion("b"+i+"-0-1");
			texture[i][2] = diffuseTextureRegion.findRegion("b"+i+"-0-2");
		}
		
		font = new BitmapFont();	
	}
 
	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		batch.setProjectionMatrix(cam.combined);
		
		shader.begin();
		shader.setUniformf("Resolution", width, height);
		shader.end();
	}
 
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.6f, 0.8f, 0.1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//reset light Z
		if (Gdx.input.isTouched()) {
			LIGHT_NORMAL.z = DEFAULT_LIGHT_Z;
			System.out.println("New light Z: "+LIGHT_NORMAL.z);
		}
		
		//update light position, normalized to screen resolution
		LIGHT_NORMAL.x = 2*Mouse.getX() / (float)Display.getWidth()-1;
		LIGHT_NORMAL.y = 2*Mouse.getY() / (float)Display.getHeight()-1;
		LIGHT_NORMAL.nor();
		
		batch.begin();
		//send a Vector4f to GLSL
		shader.setUniformf("LightNormal", LIGHT_NORMAL);
		
		//bind normal map to texture unit 1
		normalTexture.bind(1);
		
		//bind diffuse color to texture unit 0
		//important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
		diffTexture.bind(0);
		
		//draw the texture unit 0 with our shader effect applied
//		batch.draw(diffuseTextureRegion.findRegion("b2-1-0"), 50, 100);
//		batch.draw(diffuseTextureRegion.findRegion("b1-1-1"), 50, 180);
//		batch.draw(diffuseTextureRegion.findRegion("b1-1-2"), 130, 100);
		int sizeX = 200;
		int sizeY = 225;
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 5; y++) {
				if (texture[y][0] != null)
					batch.draw(
						texture[y][0],
						x*sizeX,
						y*sizeY
					);
				if (texture[y][1] != null){
					batch.draw(
						texture[y][1],
						x*sizeX+sizeX/2,
						y*sizeY+175
					);
					batch.draw(
						texture[y][1],
						x*sizeX,
						y*sizeY+125
					);
				}
				
				if (texture[y][2] != null)
					batch.draw(
						texture[y][2],
						x*sizeX+100,
						y*sizeY
					);	
			}
		}
		
		font.draw(batch, "x"+LIGHT_NORMAL.x, 20, 20);
		font.draw(batch, "y"+LIGHT_NORMAL.y, 20, 40);
		font.draw(batch, "z"+LIGHT_NORMAL.z, 20, 60);
		
		batch.end();
		
		int posX = 200;
		int posY = 200;
		int size=200;
		
		shR.begin(ShapeRenderer.ShapeType.Line);
		shR.line(
			posX +(int) ( size*LIGHT_NORMAL.x ),
			posY +size*(LIGHT_NORMAL.z-LIGHT_NORMAL.y/2f),
			posX,
			posY
		 );
		shR.end();
	}
 
	@Override
	public void pause() {
		
	}
 
	@Override
	public void resume() {
		
	}
 
	@Override
	public void dispose() {
		batch.dispose();
		diffTexture.dispose();
		normalTexture.dispose();
		shader.dispose();
	}	
}