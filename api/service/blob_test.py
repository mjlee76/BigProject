import os
from azure.storage.blob import BlobServiceClient
from dotenv import load_dotenv

load_dotenv()
connect_str = os.getenv('AZURE_STORAGE_CONNECTION_STRING')
container_name = "fastapi"
container_name_report = "report"

# BlobServiceClient 생성
blob_service_client = BlobServiceClient.from_connection_string(connect_str)
container_client = blob_service_client.get_container_client(container_name)
container_client_report = blob_service_client.get_container_client(container_name_report)

def download_file(blob_name, download_path):
    blob_client = container_client.get_blob_client(blob_name)
    with open(download_path, "wb") as download_file:
          download_file.write(blob_client.download_blob().readall())
    print(f"{blob_name} 파일을 {download_path}로 다운로드 완료.")

def upload_file(local_file_path, blob_name):
    # container_client를 통해 업로드할 blob에 접근
    blob_client = container_client_report.get_blob_client(blob_name)
    
    # 로컬 파일을 바이너리 모드로 열어서 업로드
    with open(local_file_path, "rb") as data:
        blob_client.upload_blob(data, overwrite=True)
    print(f"{local_file_path} 파일이 {blob_name}로 업로드 완료.")


