from transformers import AutoImageProcessor, AutoModelForImageClassification
from PIL import Image
from transformers import pipeline
import glob
import os

def load_image(image_dir):
    image_files = glob.glob(os.path.join(image_dir, "*.*"))
    image_path = image_files[0]
    image = Image.open(f'{image_path}').convert('RGB')
    return image

def load_model(load_directory):
    model = AutoModelForImageClassification.from_pretrained(load_directory)
    processor = AutoImageProcessor.from_pretrained(load_directory)
    classifier = pipeline("image-classification", model="Falconsai/nsfw_image_detection")
    return model, processor, classifier