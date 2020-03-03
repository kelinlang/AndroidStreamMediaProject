//
// Created by dengjun on 2019/3/27.
//

#ifndef VOICELIBDEMO_CLOUDVOICE_SHADER_UTILS_H
#define VOICELIBDEMO_CLOUDVOICE_SHADER_UTILS_H

#ifdef __cplusplus
extern "C" {
#endif
#include <malloc.h>
#include <GLES2/gl2.h>

#ifdef __cplusplus
}
#endif

namespace StreamMedia {
    namespace media {
        class ShaderUtils{
        public :
            static GLuint createProgram(const char *vertexSource, const char *fragmentSource);

             static GLuint loadShader(GLenum shaderType, const char *source);
        };
    }
}

#endif //VOICELIBDEMO_CLOUDVOICE_SHADER_UTILS_H
