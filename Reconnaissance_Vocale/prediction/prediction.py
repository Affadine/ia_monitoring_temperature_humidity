#!/usr/bin/env python
# coding: utf-8

# In[1]:


import os
import sys

import tensorflow as tf
from tensorflow import keras
from keras.models import load_model
import librosa
import numpy as np
import warnings
warnings.filterwarnings('ignore')


# In[2]:


PATH_MODEL = '../model/model.h5'
SAMPLES_TO_CONSIDER = 22050 


# In[3]:


def extraire_info_audio(file_path,num_mfcc=13, n_fft=2048, hop_length=512):
    # Découper L'audio pour assurer la cohérence de la longueur entre les différents fichiers
    signal, sample_rate = librosa.load(file_path)

    # Le nombre d'echantillon doit être supérieur à un certain seuil 
    if len(signal) >= SAMPLES_TO_CONSIDER:
        # Assurer la cohérence de la longueur du signal
        signal = signal[:SAMPLES_TO_CONSIDER]

        # Extraire les MFCCs
        MFCCs = librosa.feature.mfcc(signal, n_mfcc = num_mfcc, n_fft = n_fft, hop_length = hop_length)
        return MFCCs
    return []


# In[4]:


class_names = [
        "right",
        "off",
        "left",
        "down",
        "_background_noise_",
        "up",
        "on"
    ]

def prediction(file_path):
    
    # Charger le modele entrainé
    model = load_model(PATH_MODEL)
    # charger l'audio pour le découper
    signal, sample_rate = librosa.load(file_path)
    MFCCs = extraire_info_audio(file_path)
    if len(MFCCs) > 0 :
        audio_array = tf.expand_dims(MFCCs.T.tolist(),0)
        audio_array = audio_array[..., np.newaxis]
        predictions = model.predict(audio_array)
        score = predictions[0]
        print(score)
        print(np.argmax(score))
        print("Valeur prédit est : " + class_names[np.argmax(score)] + " avec une confiance de "
              + str(100 * np.max(score)) +"%")
        
        f = open(file_path + ".out", "w")
        f.write(str(class_names[np.argmax(score)])  + ":"  + str(100 * np.max(score))  )
        f.close()


# In[5]:


filename = (sys.argv[1:])[0]
# score = prediction('../audio_1605990141057.wav')
print(filename)
score = prediction('../' + filename)


# In[ ]:




