import os
import platform
import shutil
import datetime
import subprocess

def run(cmd):
    try:
        return subprocess.check_output(cmd, shell=True, text=True).strip()
    except:
        return "N/A"

def line():
    print("=" * 60)

def header(title):
    line()
    print(f"🚀 {title}")
    line()

header("JENKINS SYSTEM REPORT")

print(f"🖥️  Hostname : {platform.node()}")
print(f"👤 Usuario  : {os.getenv('USER')}")
print(f"🐍 Python   : {platform.python_version()}")
print(f"🕒 Hora     : {datetime.datetime.now()}")
print()

header("CPU")
print(run("lscpu | grep 'Model name'"))
print(run("lscpu | grep '^CPU(s)'"))

header("MEMORIA")
total, used, free = shutil.disk_usage("/")
print(f"💾 Disco total : {total // (1024**3)} GB")
print(f"📦 Usado       : {used // (1024**3)} GB")
print(f"📭 Libre       : {free // (1024**3)} GB")

mem = run("free -h | grep Mem")
print(mem)

header("GPU (si existe)")
print(run("nvidia-smi --query-gpu=name,memory.used,memory.total --format=csv"))

header("STATUS")
print("✅ Script ejecutado correctamente desde Jenkins")

line()
