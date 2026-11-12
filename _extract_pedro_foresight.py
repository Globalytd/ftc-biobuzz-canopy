import zipfile
from pathlib import Path

jar = Path(r'C:\Users\DanielMusigire\.gradle\caches\modules-2\files-2.1\com.pedropathing\core\3.0.1\ed369ea318ef7b76feb8de940060337d920cb6e1\core-3.0.1-sources.jar')
out = Path(r'C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz\_tmp_forsight_sources.txt')
with zipfile.ZipFile(jar) as z:
    parts = []
    for name in [
        'com/pedropathing/algorithm/ForesightConfig.java',
        'com/pedropathing/algorithm/Foresight.java',
        'com/pedropathing/math/Vector2D.java',
        'com/pedropathing/math/Matrix.java',
        'com/pedropathing/utils/Controller.java',
    ]:
        parts.append(f'### {name}\n')
        parts.append(z.read(name).decode('utf-8'))
        parts.append('\n\n')
out.write_text(''.join(parts), encoding='utf-8')
print(out)

