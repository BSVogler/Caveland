attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform vec4 v_tint;
uniform float u_red;
uniform float u_green;
uniform float u_blue;

varying vec4 v_color;
varying vec2 v_texCoords;


void main() {
    v_color = a_color*gl_Color*2.0;//multiply by 2 and use vertex color
	v_color.a = gl_Color.a*a_color.a;//use alpha of texture and multiply by blending color alpha
    v_texCoords = a_texCoord0;

    gl_Position = u_projTrans * a_position;
}