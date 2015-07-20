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
	vec3 vertex = v_color.rgb*4.0;

	//don't shade fragment's where there is no normal map => diffuse = normal
	if (abs(DiffuseColor.r-normalColor.r)<0.08 && abs(DiffuseColor.g-normalColor.g)<0.08 && abs(DiffuseColor.b-normalColor.b)<0.08) {
		gl_FragColor = vec4(vertex+vec3(0.5,0.5,0.5),v_color.a)*DiffuseColor;
	} else{
	
		vec3 N = (normalColor*2.0- 1.0);//-0.058)*1.25 to normalize because normals are not 100% correct
		N.x = -N.x;//x is flipped in texture, so fix this in shaders

		vec3 sunLight = vec3(0,0,0);
		//check if sun is shining, if not ignore it
		if (sunColor.r > 0.05 || sunColor.g > 0.05 || sunColor.b> 0.05) {
			sunLight = vec3(sunColor) * max(dot(N, sunNormal), 0.0);
		}

		vec3 moonLight = vec3(0,0,0);
		//check if moon is shining, if not ignore it
		if (moonColor.r > 0.05 || moonColor.g > 0.05 || moonColor.b> 0.05) {
			moonLight = vec3(moonColor) * max(dot(N, moonNormal), 0.0);

			//saturation
			//DiffuseColor.rgb = DiffuseColor.rgb-0.6*(DiffuseColor.rgb-vec3(dot(DiffuseColor.rgb, vec3(.222, .707, .071)) ));

			//contrast
			//DiffuseColor.rgb = ((DiffuseColor.rgb - 0.5) * max(1.0+0.4*moonColor.b, 0.0)) + 0.5;

		}
//vertice color * texture color*(sun, moon and ambient) 
//use vertice color 0,5 as a base level on which light sources are added. Vertice color os some sort of light source and not emissivity of texture.
		gl_FragColor = vec4(
			DiffuseColor.rgb*(vertex-vec3(2)+sunLight*1.3+moonLight+ambientColor.rgb*2.0)
			,DiffuseColor.a*v_color.a
		);
	}
}