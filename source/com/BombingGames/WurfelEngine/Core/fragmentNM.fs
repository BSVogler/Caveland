//attributes from vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

//our texture samplers
uniform sampler2D u_texture;   //diffuse map
uniform sampler2D u_normals;   //normal map

//values used for shading algorithm...
uniform vec3 sunNormal;        //light position, normalized
uniform vec4 sunColor;   
uniform vec4 ambientColor;
uniform vec3 moonNormal;        //light position, normalized
uniform vec4 moonColor;

void main() {
    //RGBA of our diffuse color
    vec4 DiffuseColor = texture2D(u_texture, v_texCoords);
	vec3 normalColor = texture2D(u_normals, v_texCoords).rgb;

	//don't shade fragment's where there is no normal map
	if (DiffuseColor.a >0.01 && DiffuseColor.r==normalColor.r && DiffuseColor.g==normalColor.g && DiffuseColor.b==normalColor.b) {
		gl_FragColor = DiffuseColor;
	} else{
		vec3 sunLight = vec3(0,0,0);
		vec3 moonLight = vec3(0,0,0);
	

		vec3 N = (normalColor*2.0- 1.0);//-0.058)*1.25 to normalize because normals are not 100% correct
		N.x = -N.x;//x is flipped in texture, so fix this in shaders

		//check if sun is shining, if not ignore it
		if (sunColor.r > 0.05 || sunColor.g > 0.05 || sunColor.b> 0.05) {
			sunLight = vec3(sunColor) * max(dot(N, sunNormal), 0.0);
		}

		//check if moon is shining, if not ignore it
		if (moonColor.r > 0.05 || moonColor.g > 0.05 || moonColor.b> 0.05) {
			moonLight = vec3(moonColor) * max(dot(N, moonNormal), 0.0);
		}
		gl_FragColor = vec4(v_color.rgb+vec3(0.5,0.5,0.5),v_color.a) * vec4((sunLight+moonLight+ambientColor.rgb)*DiffuseColor.rgb,DiffuseColor.a);
	}
}