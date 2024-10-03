# TensorFlow Lite Sound Recognition Android App

This project demonstrates how to integrate TensorFlow Lite for real-time sound recognition on Android using an AudioRecord object to capture audio, process it, and classify the audio using a pre-trained TensorFlow Lite model.

## Features
- Real-time audio recording using Android's AudioRecord.
- Classification of sound using a TensorFlow Lite model.
- Visual display of the captured audio waveform.

## Prerequisites

Before setting up the project, ensure that you have:
- Android Studio (4.1 or higher)
- Gradle (6.5 or higher)
- Android device running Android 5.0 (API 21) or higher

## Setup Instructions

### Step 1: Clone the Repository

bash
git clone https://github.com/Mavinkip/Audio-recognition-app
cd sound-recognition-android


### Key Sections:
1. **Dependencies:** Instructions on adding TensorFlow Lite

   // TensorFlow Lite dependencies
 implementation 'org.tensorflow:tensorflow-lite:2.10.0'

implementation 'org.tensorflow:tensorflow-lite-support:0.4.3'

 implementation 'org.tensorflow:tensorflow-lite-task-audio:0.4.2'


3. **Permissions:** Ensures microphone permissions are correctly set up.
4. **MainActivity, SoundRecognitionModel, WaveformView:**

location of the model used 

https://www.kaggle.com/models/google/yamnet/tensorFlow2/yamnet/1?tfhub-redirect=true
