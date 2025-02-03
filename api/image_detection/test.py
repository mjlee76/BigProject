from transformers import AutoModel, AutoProcessor
from PIL import Image
import torch

model = AutoModel.from_pretrained("unum-cloud/uform-gen2-qwen-500m", trust_remote_code=True)
processor = AutoProcessor.from_pretrained("unum-cloud/uform-gen2-qwen-500m", trust_remote_code=True)

prompt = "주어지는 이미지에 대해서 설명해주세요"
image = Image.open("C:/Users/User/Desktop/빅프로젝트/BigProject/api/image_detection/licensed-image.jpg")

inputs = processor(text=[prompt], images=[image], return_tensors="pt")
with torch.inference_mode():
     output = model.generate(
        **inputs,
        do_sample=False,
        use_cache=True,
        max_new_tokens=256,
        eos_token_id=151645,
        pad_token_id=processor.tokenizer.pad_token_id
    )

prompt_len = inputs["input_ids"].shape[1]
decoded_text = processor.batch_decode(output[:, prompt_len:])[0]
print(decoded_text)