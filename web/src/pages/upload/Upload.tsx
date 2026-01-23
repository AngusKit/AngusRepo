import { useState } from 'react';
import { Upload as UploadIcon, FileText, Check, X, AlertCircle, Package, Code, Copy } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useLanguage } from '@/components/LanguageProvider';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';
import { Progress } from '@/components/ui/progress';
import { Badge } from '@/components/ui/badge';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { copyToClipboard } from '@/utils/clipboard';

interface UploadFile {
  id: string;
  file: File;
  progress: number;
  status: 'pending' | 'uploading' | 'success' | 'error';
  error?: string;
}

export function Upload() {
  const { t } = useLanguage();
  const [selectedRepository, setSelectedRepository] = useState('');
  const [uploadFiles, setUploadFiles] = useState<UploadFile[]>([]);
  const [isDragging, setIsDragging] = useState(false);
  const [uploadMethod, setUploadMethod] = useState<'web' | 'cli'>('web');

  // Maven specific fields
  const [groupId, setGroupId] = useState('');
  const [artifactId, setArtifactId] = useState('');
  const [version, setVersion] = useState('');
  const [packaging, setPackaging] = useState('jar');

  const repositories = [
    { value: 'maven-releases', label: 'maven-releases (Maven - Hosted)', format: 'maven' },
    { value: 'docker-registry', label: 'docker-registry (Docker - Hosted)', format: 'docker' },
    { value: 'npm-private', label: 'npm-private (NPM - Hosted)', format: 'npm' },
    { value: 'pypi-hosted', label: 'pypi-hosted (PyPI - Hosted)', format: 'pypi' },
    { value: 'nuget-hosted', label: 'nuget-hosted (NuGet - Hosted)', format: 'nuget' },
  ];

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    
    const files = Array.from(e.dataTransfer.files);
    addFiles(files);
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const files = Array.from(e.target.files);
      addFiles(files);
    }
  };

  const addFiles = (files: File[]) => {
    const newUploadFiles: UploadFile[] = files.map(file => ({
      id: Math.random().toString(36).substr(2, 9),
      file,
      progress: 0,
      status: 'pending',
    }));
    setUploadFiles(prev => [...prev, ...newUploadFiles]);
  };

  const removeFile = (id: string) => {
    setUploadFiles(prev => prev.filter(f => f.id !== id));
  };

  const clearAll = () => {
    setUploadFiles([]);
  };

  const simulateUpload = (fileId: string) => {
    let progress = 0;
    const interval = setInterval(() => {
      progress += Math.random() * 30;
      if (progress >= 100) {
        progress = 100;
        clearInterval(interval);
        setUploadFiles(prev => prev.map(f => 
          f.id === fileId ? { ...f, progress: 100, status: 'success' } : f
        ));
      } else {
        setUploadFiles(prev => prev.map(f => 
          f.id === fileId ? { ...f, progress, status: 'uploading' } : f
        ));
      }
    }, 300);
  };

  const handleUpload = () => {
    if (!selectedRepository) {
      toast.error('Please select a repository');
      return;
    }

    if (uploadFiles.length === 0) {
      toast.error('Please select files to upload');
      return;
    }

    // Simulate upload for each file
    uploadFiles.forEach(file => {
      simulateUpload(file.id);
    });

    toast.success(t('upload.uploadSuccess'));
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  };

  const getCliInstructions = () => {
    const repo = repositories.find(r => r.value === selectedRepository);
    if (!repo) return {};

    const baseUrl = `https://repo.example.com/${selectedRepository}`;

    switch (repo.format) {
      case 'maven':
        return {
          title: 'Maven Deploy',
          commands: [
            {
              label: 'Using Maven Deploy Plugin',
              code: `mvn deploy:deploy-file \\
  -DgroupId=${groupId || 'com.example'} \\
  -DartifactId=${artifactId || 'artifact'} \\
  -Dversion=${version || '1.0.0'} \\
  -Dpackaging=${packaging} \\
  -Dfile=target/your-artifact.${packaging} \\
  -DrepositoryId=${selectedRepository} \\
  -Durl=${baseUrl}`,
            },
            {
              label: 'Add to pom.xml',
              code: `<distributionManagement>
  <repository>
    <id>${selectedRepository}</id>
    <url>${baseUrl}</url>
  </repository>
</distributionManagement>`,
            },
          ],
        };
      case 'docker':
        return {
          title: 'Docker Push',
          commands: [
            {
              label: 'Tag and Push Image',
              code: `docker tag <image-name> ${baseUrl}/<image-name>:<tag>
docker push ${baseUrl}/<image-name>:<tag>`,
            },
            {
              label: 'Login',
              code: `docker login ${baseUrl}`,
            },
          ],
        };
      case 'npm':
        return {
          title: 'NPM Publish',
          commands: [
            {
              label: 'Publish Package',
              code: `npm publish --registry ${baseUrl}`,
            },
            {
              label: 'Configure Registry',
              code: `npm config set registry ${baseUrl}`,
            },
          ],
        };
      case 'pypi':
        return {
          title: 'Python Upload',
          commands: [
            {
              label: 'Using twine',
              code: `twine upload --repository-url ${baseUrl} dist/*`,
            },
            {
              label: 'Configure .pypirc',
              code: `[distutils]
index-servers = ${selectedRepository}

[${selectedRepository}]
repository = ${baseUrl}
username = your-username
password = your-password`,
            },
          ],
        };
      case 'nuget':
        return {
          title: 'NuGet Push',
          commands: [
            {
              label: 'Push Package',
              code: `nuget push <package>.nupkg -Source ${baseUrl} -ApiKey <api-key>`,
            },
            {
              label: 'Add Source',
              code: `nuget sources add -Name ${selectedRepository} -Source ${baseUrl}`,
            },
          ],
        };
      default:
        return {};
    }
  };

  const cliInstructions = getCliInstructions();

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 dark:text-white mb-2">{t('upload.title')}</h1>
        <p className="text-gray-600 dark:text-gray-400">{t('upload.description')}</p>
      </div>

      {/* Upload Method Tabs */}
      <Tabs value={uploadMethod} onValueChange={(value) => setUploadMethod(value as 'web' | 'cli')}>
        <TabsList className="grid w-full max-w-md grid-cols-2">
          <TabsTrigger value="web">Web Upload</TabsTrigger>
          <TabsTrigger value="cli">CLI Instructions</TabsTrigger>
        </TabsList>

        <TabsContent value="web" className="space-y-6 mt-6">
          {/* Repository Selection */}
          <Card>
            <CardHeader>
              <CardTitle>{t('upload.selectRepository')}</CardTitle>
              <CardDescription>Choose the target repository for your artifacts</CardDescription>
            </CardHeader>
            <CardContent>
              <Select value={selectedRepository} onValueChange={setSelectedRepository}>
                <SelectTrigger>
                  <SelectValue placeholder="Select a repository..." />
                </SelectTrigger>
                <SelectContent>
                  {repositories.map(repo => (
                    <SelectItem key={repo.value} value={repo.value}>
                      {repo.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </CardContent>
          </Card>

          {/* Maven Upload Settings */}
          {selectedRepository && repositories.find(r => r.value === selectedRepository)?.format === 'maven' && (
            <Card>
              <CardHeader>
                <CardTitle>{t('upload.uploadSettings')}</CardTitle>
                <CardDescription>Maven artifact coordinates</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="groupId">{t('upload.groupId')}</Label>
                    <Input
                      id="groupId"
                      placeholder="com.example"
                      value={groupId}
                      onChange={(e) => setGroupId(e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="artifactId">{t('upload.artifactId')}</Label>
                    <Input
                      id="artifactId"
                      placeholder="my-artifact"
                      value={artifactId}
                      onChange={(e) => setArtifactId(e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="version">{t('upload.version')}</Label>
                    <Input
                      id="version"
                      placeholder="1.0.0"
                      value={version}
                      onChange={(e) => setVersion(e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="packaging">{t('upload.packaging')}</Label>
                    <Select value={packaging} onValueChange={setPackaging}>
                      <SelectTrigger id="packaging">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="jar">JAR</SelectItem>
                        <SelectItem value="war">WAR</SelectItem>
                        <SelectItem value="pom">POM</SelectItem>
                        <SelectItem value="ear">EAR</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* File Upload Area */}
          <Card>
            <CardHeader>
              <CardTitle>{t('upload.selectFiles')}</CardTitle>
              <CardDescription>
                {uploadFiles.length > 0
                  ? `${uploadFiles.length} file(s) selected`
                  : 'No files selected'}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div
                className={`border-2 border-dashed rounded-lg p-12 text-center transition-colors ${
                  isDragging
                    ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/10'
                    : 'border-gray-300 dark:border-gray-600 hover:border-purple-400'
                }`}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
              >
                <UploadIcon className="size-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-900 dark:text-white mb-2">
                  {t('upload.dragAndDrop')}
                </p>
                <p className="text-gray-600 dark:text-gray-400 text-sm mb-4">
                  {t('upload.or')}
                </p>
                <Button variant="outline" asChild>
                  <label>
                    {t('upload.browse')}
                    <input
                      type="file"
                      multiple
                      className="hidden"
                      onChange={handleFileSelect}
                    />
                  </label>
                </Button>
              </div>

              {/* File List */}
              {uploadFiles.length > 0 && (
                <div className="mt-6 space-y-3">
                  <div className="flex items-center justify-between">
                    <p className="text-sm text-gray-700 dark:text-gray-300">
                      {t('upload.selectedFiles')}
                    </p>
                    <Button variant="ghost" size="sm" onClick={clearAll}>
                      {t('upload.clearAll')}
                    </Button>
                  </div>
                  {uploadFiles.map(uploadFile => (
                    <div
                      key={uploadFile.id}
                      className="flex items-center gap-3 p-3 rounded-lg border border-gray-200 dark:border-gray-700"
                    >
                      <FileText className="size-8 text-gray-400 flex-shrink-0" />
                      <div className="flex-1 min-w-0">
                        <p className="text-sm text-gray-900 dark:text-white truncate">
                          {uploadFile.file.name}
                        </p>
                        <p className="text-xs text-gray-600 dark:text-gray-400">
                          {formatFileSize(uploadFile.file.size)}
                        </p>
                        {uploadFile.status === 'uploading' && (
                          <Progress value={uploadFile.progress} className="h-1 mt-2" />
                        )}
                      </div>
                      {uploadFile.status === 'success' && (
                        <Check className="size-5 text-green-600 dark:text-green-400 flex-shrink-0" />
                      )}
                      {uploadFile.status === 'error' && (
                        <AlertCircle className="size-5 text-red-600 dark:text-red-400 flex-shrink-0" />
                      )}
                      {uploadFile.status === 'pending' && (
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => removeFile(uploadFile.id)}
                        >
                          <X className="size-4" />
                        </Button>
                      )}
                    </div>
                  ))}
                </div>
              )}

              {/* Upload Button */}
              {uploadFiles.length > 0 && (
                <Button
                  className="w-full mt-6"
                  onClick={handleUpload}
                  disabled={!selectedRepository || uploadFiles.some(f => f.status === 'uploading')}
                >
                  <UploadIcon className="mr-2 size-4" />
                  {t('upload.title')}
                </Button>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="cli" className="space-y-6 mt-6">
          {!selectedRepository ? (
            <Alert>
              <AlertCircle className="size-4" />
              <AlertDescription>
                Please select a repository first to view CLI instructions
              </AlertDescription>
            </Alert>
          ) : (
            <Card>
              <CardHeader>
                <CardTitle>{cliInstructions.title}</CardTitle>
                <CardDescription>
                  Command line instructions for {selectedRepository}
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                {cliInstructions.commands?.map((cmd: any, index: number) => (
                  <div key={index}>
                    <Label className="mb-2 block">{cmd.label}</Label>
                    <div className="relative">
                      <pre className="bg-gray-900 dark:bg-gray-950 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm">
                        <code>{cmd.code}</code>
                      </pre>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="absolute top-2 right-2"
                        onClick={() => {
                          copyToClipboard(cmd.code);
                          toast.success('Copied to clipboard');
                        }}
                      >
                        <Copy className="size-4" />
                      </Button>
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}