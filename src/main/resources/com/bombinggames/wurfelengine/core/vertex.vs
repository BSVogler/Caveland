attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoords;


void main() {
#ifdef GL_ES //GL ES 2.0 doesn't know gl_Color
    v_color = a_color*2.0;
    v_color.a = a_color.a;
#else
     v_color.rgb = a_color.rgb*gl_Color.rgb*2.0;//multiply by 2 and use vertex color because normal vertex color is expected to be at 0.5
    v_color.a = gl_Color.a*a_color.a;//use alpha of texture and multiply by blending color alpha
#endif
    v_texCoords = a_texCoord0;

    gl_Position = u_projTrans * a_position;
}