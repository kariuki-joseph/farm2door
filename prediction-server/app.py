from flask import Flask, request, jsonify
import pickle
import numpy as np

# Load the pickled model (replace with your actual model path)
model_path = "model.pkl"
with open(model_path, "rb") as f:
    model = pickle.load(f)

app = Flask(__name__)

@app.route("/", methods=["GET"])
def home():
    return "Welcome to the Farm2Door recommendation server!"

@app.route("/predict", methods=["GET"])
def predict():
    try:
        # Get prediction period from request args (ensure it's a number)
        duration = int(request.args.get("duration", default=0))

        # Validate prediction period (optional, adjust as needed)
        if duration <= 0:
            return jsonify({"error": "Prediction period must be positive."}), 400

        # Make predictions using the loaded model
        predictions = model.predict(n_periods=duration)

        # Convert predictions to a list for JSON serialization
        prediction_list = predictions.tolist()

        return jsonify({"predictions": prediction_list})

    except (ValueError, KeyError):
        return jsonify({"error": "Invalid request data."}), 400

    except Exception as e:
        # Handle unexpected errors gracefully (e.g., logging)
        return jsonify({"error": "An error occurred."}), 500

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, debug=True)
