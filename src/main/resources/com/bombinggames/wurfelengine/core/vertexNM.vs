attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoords;


void main() {
    v_color = a_color;//multiply by 2 and use vertex color because normal vertex color is expected to be at 0.5
    v_texCoords = a_texCoord0;

    gl_Position = u_projTrans * a_position;
}