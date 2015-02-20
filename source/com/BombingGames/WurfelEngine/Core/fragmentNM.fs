//attributes from vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

//our texture samplers
uniform sampler2D u_texture;   //diffuse map
uniform sampler2D u_normals;   //normal map

//values used for shading algorithm...
uniform vec3 sunNormal;        //light position, normalized
uniform vec4 sunColor;      //light RGBA -- alpha is intensity
uniform vec4 ambientColor;    //ambient RGBA -- alpha is intensity 
uniform vec3 moonNormal;        //light position, normalized
uniform vec4 moonColor;      //light RGBA -- alpha is intensity

void main() {
    //RGBA of our diffuse color
    vec4 DiffuseColor = texture2D(u_texture, v_texCoords);

    //RGB of our normal map
    vec3 N = (texture2D(u_normals, v_texCoords).rgb*2.0- 1.0);//-0.058)*1.25 to normalize because normals are not 100% correct

	N.x = -N.x;//x is flipped in texture, so fix this in shaders
   // vec3 L = normalize(LightNormal);

    //Pre-multiply light color with intensity
    vec4 DiffuseLS = sunColor * max(dot(N, sunNormal), 0.0);

    //calculate attenuation
    //float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );

    //the calculation which brings it all together
    gl_FragColor = v_color * (DiffuseLS+ambientColor)*DiffuseColor;
}