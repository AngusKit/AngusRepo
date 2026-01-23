import { useState } from 'react';
import { Copy, Check, Terminal, Code, FileText } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { copyToClipboard } from '@/utils/clipboard';
import { toast } from 'sonner';

interface RepositorySetupGuideProps {
  format: string;
  repositoryName: string;
  repositoryUrl: string;
}

export function RepositorySetupGuide({ format, repositoryName, repositoryUrl }: RepositorySetupGuideProps) {
  const [copiedItem, setCopiedItem] = useState('');

  const handleCopy = async (text: string, itemId: string) => {
    const success = await copyToClipboard(text);
    if (success) {
      setCopiedItem(itemId);
      toast.success('配置已复制到剪贴板');
      setTimeout(() => setCopiedItem(''), 2000);
    } else {
      toast.error('复制失败');
    }
  };

  const renderMavenSetup = () => {
    const settingsXml = `<settings>
  <servers>
    <server>
      <id>${repositoryName}</id>
      <username>your-username</username>
      <password>your-password</password>
    </server>
  </servers>
  
  <mirrors>
    <mirror>
      <id>${repositoryName}-mirror</id>
      <mirrorOf>*</mirrorOf>
      <url>${repositoryUrl}</url>
    </mirror>
  </mirrors>
</settings>`;

    const pomXml = `<project>
  <repositories>
    <repository>
      <id>${repositoryName}</id>
      <url>${repositoryUrl}</url>
      <releases>
        <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
    </repository>
  </repositories>
  
  <distributionManagement>
    <repository>
      <id>${repositoryName}</id>
      <url>${repositoryUrl}</url>
    </repository>
  </distributionManagement>
</project>`;

    const deployCommand = `mvn deploy -DaltDeploymentRepository=${repositoryName}::default::${repositoryUrl}`;

    return (
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  settings.xml 配置
                </CardTitle>
                <CardDescription>在 ~/.m2/settings.xml 中添加以下配置</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(settingsXml, 'maven-settings')}
              >
                {copiedItem === 'maven-settings' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{settingsXml}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  pom.xml 配置
                </CardTitle>
                <CardDescription>在项目的 pom.xml 中添加仓库配置</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(pomXml, 'maven-pom')}
              >
                {copiedItem === 'maven-pom' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{pomXml}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  发布制品
                </CardTitle>
                <CardDescription>使用 Maven 命令发布制品到仓库</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(deployCommand, 'maven-deploy')}
              >
                {copiedItem === 'maven-deploy' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{deployCommand}</code>
            </pre>
          </CardContent>
        </Card>
      </div>
    );
  };

  const renderDockerSetup = () => {
    const loginCommand = `docker login ${repositoryUrl.replace('https://', '')}
# 输入用户名和密码`;

    const pullCommand = `# 拉取镜像
docker pull ${repositoryUrl.replace('https://', '')}/your-image:tag`;

    const pushCommand = `# 标记镜像
docker tag your-image:tag ${repositoryUrl.replace('https://', '')}/your-image:tag

# 推送镜像
docker push ${repositoryUrl.replace('https://', '')}/your-image:tag`;

    const dockerfileExample = `FROM ${repositoryUrl.replace('https://', '')}/base-image:latest

WORKDIR /app
COPY . .

RUN npm install
CMD ["npm", "start"]`;

    return (
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  Docker 登录
                </CardTitle>
                <CardDescription>首先需要登录到私有 Docker 仓库</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(loginCommand, 'docker-login')}
              >
                {copiedItem === 'docker-login' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{loginCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  拉取镜像
                </CardTitle>
                <CardDescription>从仓库拉取 Docker 镜像</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(pullCommand, 'docker-pull')}
              >
                {copiedItem === 'docker-pull' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{pullCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  推送镜像
                </CardTitle>
                <CardDescription>将 Docker 镜像推送到仓库</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(pushCommand, 'docker-push')}
              >
                {copiedItem === 'docker-push' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{pushCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  Dockerfile 示例
                </CardTitle>
                <CardDescription>在 Dockerfile 中使用私有仓库的基础镜像</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(dockerfileExample, 'dockerfile')}
              >
                {copiedItem === 'dockerfile' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{dockerfileExample}</code>
            </pre>
          </CardContent>
        </Card>
      </div>
    );
  };

  const renderNpmSetup = () => {
    const npmrcConfig = `registry=${repositoryUrl}
//repo.example.com/:_authToken=YOUR_AUTH_TOKEN
always-auth=true`;

    const npmrcScoped = `@your-scope:registry=${repositoryUrl}
//repo.example.com/:_authToken=YOUR_AUTH_TOKEN`;

    const installCommand = `# 安装依赖
npm install

# 安装指定包
npm install package-name`;

    const publishCommand = `# 发布包
npm publish

# 发布到指定仓库
npm publish --registry=${repositoryUrl}`;

    const packageJson = `{
  "name": "@your-scope/package-name",
  "version": "1.0.0",
  "publishConfig": {
    "registry": "${repositoryUrl}"
  }
}`;

    return (
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  .npmrc 配置（全局）
                </CardTitle>
                <CardDescription>在 ~/.npmrc 或项目根目录创建配置文件</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(npmrcConfig, 'npmrc-global')}
              >
                {copiedItem === 'npmrc-global' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{npmrcConfig}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  .npmrc 配置（作用域）
                </CardTitle>
                <CardDescription>仅对特定作用域的包使用私有仓库</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(npmrcScoped, 'npmrc-scoped')}
              >
                {copiedItem === 'npmrc-scoped' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{npmrcScoped}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  安装依赖
                </CardTitle>
                <CardDescription>使用 npm 安装包</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(installCommand, 'npm-install')}
              >
                {copiedItem === 'npm-install' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{installCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  发布包
                </CardTitle>
                <CardDescription>将 npm 包发布到仓库</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(publishCommand, 'npm-publish')}
              >
                {copiedItem === 'npm-publish' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{publishCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  package.json 配置
                </CardTitle>
                <CardDescription>在 package.json 中指定发布仓库</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(packageJson, 'package-json')}
              >
                {copiedItem === 'package-json' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{packageJson}</code>
            </pre>
          </CardContent>
        </Card>
      </div>
    );
  };

  const renderNuGetSetup = () => {
    const nugetConfig = `<?xml version="1.0" encoding="utf-8"?>
<configuration>
  <packageSources>
    <add key="${repositoryName}" value="${repositoryUrl}" />
  </packageSources>
  <packageSourceCredentials>
    <${repositoryName}>
      <add key="Username" value="your-username" />
      <add key="ClearTextPassword" value="your-password" />
    </${repositoryName}>
  </packageSourceCredentials>
</configuration>`;

    const installCommand = `# 安装包
dotnet add package PackageName

# 从指定源安装
dotnet add package PackageName --source ${repositoryUrl}`;

    const pushCommand = `# 发布包
dotnet nuget push package.nupkg --source ${repositoryUrl} --api-key YOUR_API_KEY`;

    const restoreCommand = `# 还原依赖
dotnet restore --source ${repositoryUrl}`;

    return (
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  NuGet.Config 配置
                </CardTitle>
                <CardDescription>在项目根目录或 ~/.nuget/NuGet 创建配置文件</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(nugetConfig, 'nuget-config')}
              >
                {copiedItem === 'nuget-config' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{nugetConfig}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  安装包
                </CardTitle>
                <CardDescription>使用 dotnet CLI 安装 NuGet 包</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(installCommand, 'nuget-install')}
              >
                {copiedItem === 'nuget-install' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{installCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  发布包
                </CardTitle>
                <CardDescription>将 NuGet 包推送到仓库</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(pushCommand, 'nuget-push')}
              >
                {copiedItem === 'nuget-push' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{pushCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  还原依赖
                </CardTitle>
                <CardDescription>还原项目依赖</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(restoreCommand, 'nuget-restore')}
              >
                {copiedItem === 'nuget-restore' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{restoreCommand}</code>
            </pre>
          </CardContent>
        </Card>
      </div>
    );
  };

  const renderPyPISetup = () => {
    const pipConfig = `[global]
index-url = ${repositoryUrl}/simple
trusted-host = ${repositoryUrl.replace('https://', '').replace('http://', '')}

[install]
trusted-host = ${repositoryUrl.replace('https://', '').replace('http://', '')}`;

    const piprcConfig = `[distutils]
index-servers =
    ${repositoryName}

[${repositoryName}]
repository = ${repositoryUrl}
username = your-username
password = your-password`;

    const installCommand = `# 安装包
pip install package-name

# 从指定源安装
pip install package-name --index-url ${repositoryUrl}/simple

# 安装 requirements.txt
pip install -r requirements.txt`;

    const uploadCommand = `# 使用 twine 上传
twine upload --repository-url ${repositoryUrl} dist/*

# 或在 .pypirc 配置后
twine upload -r ${repositoryName} dist/*`;

    const setupPy = `from setuptools import setup

setup(
    name='your-package',
    version='1.0.0',
    packages=['your_package'],
    # ... 其他配置
)`;

    return (
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  pip.conf 配置
                </CardTitle>
                <CardDescription>Linux/Mac: ~/.pip/pip.conf，Windows: %APPDATA%\pip\pip.ini</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(pipConfig, 'pip-config')}
              >
                {copiedItem === 'pip-config' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{pipConfig}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  .pypirc 配置
                </CardTitle>
                <CardDescription>在 ~/.pypirc 配置上传凭证</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(piprcConfig, 'pypirc')}
              >
                {copiedItem === 'pypirc' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{piprcConfig}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  安装包
                </CardTitle>
                <CardDescription>使用 pip 安装 Python 包</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(installCommand, 'pip-install')}
              >
                {copiedItem === 'pip-install' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{installCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  上传包
                </CardTitle>
                <CardDescription>使用 twine 上传 Python 包</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(uploadCommand, 'pip-upload')}
              >
                {copiedItem === 'pip-upload' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{uploadCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  setup.py 示例
                </CardTitle>
                <CardDescription>创建可发布的 Python 包</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(setupPy, 'setup-py')}
              >
                {copiedItem === 'setup-py' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{setupPy}</code>
            </pre>
          </CardContent>
        </Card>
      </div>
    );
  };

  const renderAptSetup = () => {
    const sourcesList = `# 在 /etc/apt/sources.list.d/${repositoryName}.list 添加
deb ${repositoryUrl} focal main
deb-src ${repositoryUrl} focal main`;

    const gpgKey = `# 添加 GPG 密钥
wget -qO - ${repositoryUrl}/gpg.key | sudo apt-key add -

# 或使用新方法
wget -qO - ${repositoryUrl}/gpg.key | sudo gpg --dearmor -o /usr/share/keyrings/${repositoryName}.gpg`;

    const updateCommand = `# 更新包索引
sudo apt update

# 安装包
sudo apt install package-name`;

    const uploadExample = `# 构建 deb 包
dpkg-deb --build package-dir

# 上传到仓库（需要配置 reprepro 或 aptly）
# 具体方法取决于仓库配置`;

    return (
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  sources.list 配置
                </CardTitle>
                <CardDescription>添加 APT 源配置</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(sourcesList, 'apt-sources')}
              >
                {copiedItem === 'apt-sources' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{sourcesList}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  添加 GPG 密钥
                </CardTitle>
                <CardDescription>信任仓库的 GPG 签名密钥</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(gpgKey, 'apt-gpg')}
              >
                {copiedItem === 'apt-gpg' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{gpgKey}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  安装包
                </CardTitle>
                <CardDescription>使用 apt 安装软件包</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(updateCommand, 'apt-install')}
              >
                {copiedItem === 'apt-install' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{updateCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Code className="size-5" />
                  上传包
                </CardTitle>
                <CardDescription>构建和上传 deb 包</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(uploadExample, 'apt-upload')}
              >
                {copiedItem === 'apt-upload' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{uploadExample}</code>
            </pre>
          </CardContent>
        </Card>
      </div>
    );
  };

  const renderYumSetup = () => {
    const yumRepo = `# 在 /etc/yum.repos.d/${repositoryName}.repo 创建
[${repositoryName}]
name=${repositoryName} Repository
baseurl=${repositoryUrl}
enabled=1
gpgcheck=1
gpgkey=${repositoryUrl}/RPM-GPG-KEY`;

    const installCommand = `# 清理缓存
sudo yum clean all

# 安装包
sudo yum install package-name

# 或使用 dnf
sudo dnf install package-name`;

    const searchCommand = `# 搜索包
yum search package-name

# 查看包信息
yum info package-name`;

    const buildRpm = `# 构建 RPM 包
rpmbuild -ba package.spec

# 签名 RPM 包
rpm --addsign package.rpm`;

    return (
      <div className="space-y-6">
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="size-5" />
                  YUM 仓库配置
                </CardTitle>
                <CardDescription>在 /etc/yum.repos.d/ 创建 repo 文件</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(yumRepo, 'yum-repo')}
              >
                {copiedItem === 'yum-repo' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{yumRepo}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  安装包
                </CardTitle>
                <CardDescription>使用 yum/dnf 安装软件包</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(installCommand, 'yum-install')}
              >
                {copiedItem === 'yum-install' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{installCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  搜索包
                </CardTitle>
                <CardDescription>搜索和查看包信息</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(searchCommand, 'yum-search')}
              >
                {copiedItem === 'yum-search' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{searchCommand}</code>
            </pre>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Code className="size-5" />
                  构建 RPM 包
                </CardTitle>
                <CardDescription>构建和签名 RPM 包</CardDescription>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handleCopy(buildRpm, 'rpm-build')}
              >
                {copiedItem === 'rpm-build' ? (
                  <><Check className="mr-2 size-4" />已复制</>
                ) : (
                  <><Copy className="mr-2 size-4" />复制</>
                )}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <pre className="bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
              <code>{buildRpm}</code>
            </pre>
          </CardContent>
        </Card>
      </div>
    );
  };

  const renderSetupContent = () => {
    switch (format) {
      case 'Maven':
        return renderMavenSetup();
      case 'Docker':
        return renderDockerSetup();
      case 'NPM':
        return renderNpmSetup();
      case 'NuGet':
        return renderNuGetSetup();
      case 'PyPI':
        return renderPyPISetup();
      case 'Apt':
        return renderAptSetup();
      case 'Yum':
        return renderYumSetup();
      default:
        return (
          <Card>
            <CardContent className="pt-6">
              <div className="text-center py-8">
                <Code className="size-12 text-gray-400 mx-auto mb-3" />
                <p className="text-gray-600 dark:text-gray-400">
                  暂不支持 {format} 格式的接入配置指南
                </p>
              </div>
            </CardContent>
          </Card>
        );
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
            {format} 接入配置
          </h3>
          <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
            按照以下步骤配置客户端以使用此仓库
          </p>
        </div>
        <div className="flex items-center gap-2">
          <div className="px-3 py-1 bg-blue-100 dark:bg-blue-900/30 rounded-lg">
            <p className="text-sm text-blue-700 dark:text-blue-300">仓库: {repositoryName}</p>
          </div>
        </div>
      </div>

      {renderSetupContent()}
    </div>
  );
}
