package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *An entity wich is animated.
 * @author Benedikt
 */
public class AnimatedEntity extends AbstractEntity implements Animatable {
    private final int[] animationsduration;
    private int counter = 0;
    private boolean running;
    private final boolean loop;
    
   /**
     * Create an entity with an animation with an array wich has the time of every animation step in ms in it.
     * @param id The id of the object
     * @param value the starting value
     * @param  autostart True when it should automatically start.
     * @param loop Set to true when it should loop, when false it stops after one time.
     * @param animationsinformation  the time in ms for each animation step
     */
    protected AnimatedEntity(int id, int value, int[] animationsinformation, boolean autostart, boolean loop){
        super(id);
        this.animationsduration = animationsinformation;
        this.running = autostart;
        this.loop = loop;
    }
    
   /**
     * updates the entity and the animation.
     * @param delta the time wich has passed since last update
     */
    @Override
    public void update(float delta) {
        if (running) {
            counter += delta;
            if (counter >= animationsduration[getValue()]){
                setValue(getValue()+1);
                counter=0;
                if (getValue() >= animationsduration.length)//if over animation array
                    if (loop)
                        setValue(0);
                    else{//delete
                        setHidden(true);
                        setValue(getValue()-1);
                        destroy();
                    }
            }
        }
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }
}