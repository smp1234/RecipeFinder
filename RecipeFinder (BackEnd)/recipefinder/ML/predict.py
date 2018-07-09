import sys
from azure.cognitiveservices.vision.customvision.prediction import prediction_endpoint

prediction_key = "78d6ea6aa910467a8b503b0718a77a0b"
project_id = "12d9f517-1795-439c-8880-de95f5bfa748"

predictor = prediction_endpoint.PredictionEndpoint(prediction_key)
image_url = sys.argv[1]

with open(image_url, mode="rb") as test_data:
    results = predictor.predict_image(project_id, test_data)
    prediction = results.predictions
    if(prediction[0].probability * 100) >= 60.0:
        print(prediction[0].tag_name.lower())