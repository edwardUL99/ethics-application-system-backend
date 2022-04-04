import os
import shutil
import tempfile
import zipfile


def backup(upload_dir: str, destination: str):
    """
    Back up the upload directory to a zip file in the destination directory
    :param upload_dir: the upload directory to backup
    :param destination: the destination directory to backup to
    :return: the path of the zip archive
    """
    save_cwd = os.getcwd()
    os.chdir(upload_dir)
    shutil.make_archive('files', 'zip')
    shutil.move('files.zip', destination)
    os.chdir(save_cwd)

    return destination


def restore(upload_dir: str, files_zip: str):
    """
    Restore the files from the files zip to the upload directory
    :param upload_dir: the directory to upload files to
    :param files_zip: the path to the files zip
    """
    saved = os.getcwd()

    if not os.path.isdir(upload_dir):
        os.makedirs(upload_dir)

    with tempfile.TemporaryDirectory() as tmp_dir:
        with zipfile.ZipFile(files_zip, 'r') as zipped:
            os.chdir(tmp_dir)
            zipped.extractall()

            for file in os.listdir(tmp_dir):
                file = os.path.join(tmp_dir, file)
                os.system(f'cp -r {file} {upload_dir}')

            os.chdir(saved)

