import { useState, useRef } from 'react';
import { 
  Upload, X, File, CheckCircle, AlertCircle, Loader2, 
  FolderUp, Package, Archive, Container, Box, Trash2,
  FileText, Image as ImageIcon, Code2, ChevronDown
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Progress } from '@/components/ui/progress';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Separator } from '@/components/ui/separator';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';

interface UploadFile {
  id: string;
  file: File;
  status: 'pending' | 'uploading' | 'success' | 'error';
  progress: number;
  error?: string;
}

export function ArtifactUpload() {
  const { t } = useLanguage();
  const [selectedRepository, setSelectedRepository] = useState('maven-releases');
  const [uploadFormat, setUploadFormat] = useState<'Maven' | 'Docker' | 'NPM' | 'PyPI' | 'NuGet' | 'Raw'>('Maven');
  const [uploadFiles, setUploadFiles] = useState<UploadFile[]>([]);
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  
  // Maven specific fields
  const [groupId, setGroupId] = useState('com.example');
  const [artifactId, setArtifactId] = useState('');
  const [version, setVersion] = useState('1.0.0');
  const [packaging, setPackaging] = useState('jar');
  
  // Docker specific fields
  const [imageName, setImageName] = useState('');
  const [imageTag, setImageTag] = useState('latest');
  
  // NPM specific fields
  const [packageName, setPackageName] = useState('');
  const [packageVersion, setPackageVersion] = useState('');

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
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
    const newFiles: UploadFile[] = files.map(file => ({
      id: Math.random().toString(36).substr(2, 9),
      file,
      status: 'pending',
      progress: 0
    }));
    setUploadFiles(prev => [...prev, ...newFiles]);
  };

  const removeFile = (id: string) => {
    setUploadFiles(prev => prev.filter(f => f.id !== id));
  };

  const clearAll = () => {
    setUploadFiles([]);
  };

  const simulateUpload = (fileId: string) => {
    setUploadFiles(prev => prev.map(f => 
      f.id === fileId ? { ...f, status: 'uploading' as const, progress: 0 } : f
    ));

    const interval = setInterval(() => {
      setUploadFiles(prev => {
        const file = prev.find(f => f.id === fileId);
        if (!file) {
          clearInterval(interval);
          return prev;
        }

        if (file.progress >= 100) {
          clearInterval(interval);
          return prev.map(f => 
            f.id === fileId ? { ...f, status: 'success' as const, progress: 100 } : f
          );
        }

        return prev.map(f => 
          f.id === fileId ? { ...f, progress: Math.min(f.progress + 10, 100) } : f
        );
      });
    }, 200);
  };

  const handleUpload = () => {
    if (uploadFiles.length === 0) {
      toast.error(t('upload.selectFiles'));
      return;
    }

    // Validate Maven fields
    if (uploadFormat === 'Maven' && !artifactId) {
      toast.error(t('upload.artifactIdRequired'));
      return;
    }

    // Validate Docker fields
    if (uploadFormat === 'Docker' && !imageName) {
      toast.error(t('upload.imageNameRequired'));
      return;
    }

    // Start upload simulation
    uploadFiles.forEach(file => {
      if (file.status === 'pending') {
        simulateUpload(file.id);
      }
    });

    toast.success(t('upload.uploadStarted'));
  };

  const getFileIcon = (fileName: string) => {
    const ext = fileName.split('.').pop()?.toLowerCase();
    switch (ext) {
      case 'jar':
      case 'war':
      case 'ear':
        return <Archive className="size-8 text-purple-500" />;
      case 'json':
      case 'xml':
      case 'pom':
        return <Code2 className="size-8 text-orange-500" />;
      case 'tar':
      case 'gz':
      case 'zip':
        return <FolderUp className="size-8 text-blue-500" />;
      case 'png':
      case 'jpg':
      case 'svg':
        return <ImageIcon className="size-8 text-green-500" />;
      default:
        return <File className="size-8 text-gray-400" />;
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  };

  const totalFiles = uploadFiles.length;
  const completedFiles = uploadFiles.filter(f => f.status === 'success').length;
  const errorFiles = uploadFiles.filter(f => f.status === 'error').length;
  const uploadingFiles = uploadFiles.filter(f => f.status === 'uploading').length;

  return (
    <div className="flex flex-col h-[calc(100vh-8rem)]">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl text-gray-900 dark:text-white mb-2">
          {t('upload.title')}
        </h1>
        <p className="text-gray-600 dark:text-gray-400">
          {t('upload.description')}
        </p>
      </div>

      <div className="grid grid-cols-3 gap-6 flex-1 min-h-0">
        {/* Left Panel - Upload Configuration */}
        <div className="col-span-1 space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('upload.uploadSettings')}</CardTitle>
              <CardDescription>{t('upload.configureUploadSettings')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label htmlFor="repository">{t('upload.selectRepository')}</Label>
                <Select value={selectedRepository} onValueChange={setSelectedRepository}>
                  <SelectTrigger id="repository">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="maven-releases">maven-releases</SelectItem>
                    <SelectItem value="maven-snapshots">maven-snapshots</SelectItem>
                    <SelectItem value="docker-registry">docker-registry</SelectItem>
                    <SelectItem value="npm-public">npm-public</SelectItem>
                    <SelectItem value="pypi-public">pypi-public</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div>
                <Label htmlFor="format">{t('common.format')}</Label>
                <Select value={uploadFormat} onValueChange={(v) => setUploadFormat(v as any)}>
                  <SelectTrigger id="format">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Maven">Maven</SelectItem>
                    <SelectItem value="Docker">Docker</SelectItem>
                    <SelectItem value="NPM">NPM</SelectItem>
                    <SelectItem value="PyPI">PyPI</SelectItem>
                    <SelectItem value="NuGet">NuGet</SelectItem>
                    <SelectItem value="Raw">Raw</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <Separator />

              {/* Format-specific fields */}
              {uploadFormat === 'Maven' && (
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="groupId">{t('upload.groupId')}</Label>
                    <Input
                      id="groupId"
                      value={groupId}
                      onChange={(e) => setGroupId(e.target.value)}
                      placeholder="com.example"
                    />
                  </div>
                  <div>
                    <Label htmlFor="artifactId">{t('upload.artifactId')} *</Label>
                    <Input
                      id="artifactId"
                      value={artifactId}
                      onChange={(e) => setArtifactId(e.target.value)}
                      placeholder="my-artifact"
                    />
                  </div>
                  <div>
                    <Label htmlFor="version">{t('common.version')}</Label>
                    <Input
                      id="version"
                      value={version}
                      onChange={(e) => setVersion(e.target.value)}
                      placeholder="1.0.0"
                    />
                  </div>
                  <div>
                    <Label htmlFor="packaging">{t('upload.packaging')}</Label>
                    <Select value={packaging} onValueChange={setPackaging}>
                      <SelectTrigger id="packaging">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="jar">JAR</SelectItem>
                        <SelectItem value="war">WAR</SelectItem>
                        <SelectItem value="ear">EAR</SelectItem>
                        <SelectItem value="pom">POM</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
              )}

              {uploadFormat === 'Docker' && (
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="imageName">{t('upload.imageName')} *</Label>
                    <Input
                      id="imageName"
                      value={imageName}
                      onChange={(e) => setImageName(e.target.value)}
                      placeholder="nginx"
                    />
                  </div>
                  <div>
                    <Label htmlFor="tag">{t('upload.tag')}</Label>
                    <Input
                      id="tag"
                      value={imageTag}
                      onChange={(e) => setImageTag(e.target.value)}
                      placeholder="latest"
                    />
                  </div>
                  <div className="p-3 bg-blue-50 dark:bg-blue-950/20 rounded-lg">
                    <p className="text-sm text-blue-900 dark:text-blue-100 font-medium mb-2">
                      {t('upload.dockerPushCommand')}
                    </p>
                    <code className="text-xs bg-gray-900 dark:bg-gray-950 text-gray-100 px-3 py-2 rounded block font-mono">
                      docker push registry.example.com/{imageName}:{imageTag}
                    </code>
                  </div>
                </div>
              )}

              {uploadFormat === 'NPM' && (
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="packageName">{t('upload.packageName')} *</Label>
                    <Input
                      id="packageName"
                      value={packageName}
                      onChange={(e) => setPackageName(e.target.value)}
                      placeholder="my-package"
                    />
                  </div>
                  <div>
                    <Label htmlFor="packageVersion">{t('common.version')}</Label>
                    <Input
                      id="packageVersion"
                      value={packageVersion}
                      onChange={(e) => setPackageVersion(e.target.value)}
                      placeholder="1.0.0"
                    />
                  </div>
                  <div className="p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
                    <p className="text-xs text-gray-600 dark:text-gray-400 mb-2">
                      {t('upload.npmPublishTip')}
                    </p>
                    <code className="text-xs text-gray-900 dark:text-white">
                      npm publish --registry https://registry.example.com
                    </code>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Right Panel - File Upload Area */}
        <div className="col-span-2 flex flex-col space-y-6">
          {/* Drop Zone */}
          <Card 
            className={`border-2 border-dashed transition-colors ${
              isDragging 
                ? 'border-blue-500 bg-blue-50 dark:bg-blue-950/20' 
                : 'border-gray-300 dark:border-gray-700'
            }`}
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
          >
            <CardContent className="flex flex-col items-center justify-center py-12">
              <div className={`size-16 rounded-full flex items-center justify-center mb-4 ${
                isDragging 
                  ? 'bg-blue-100 dark:bg-blue-900/30' 
                  : 'bg-gray-100 dark:bg-gray-800'
              }`}>
                <Upload className={`size-8 ${
                  isDragging ? 'text-blue-600 dark:text-blue-400' : 'text-gray-400'
                }`} />
              </div>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                {t('upload.dragAndDrop')}
              </h3>
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">
                {t('upload.or')}
              </p>
              <Button onClick={() => fileInputRef.current?.click()}>
                <FolderUp className="mr-2 size-4" />
                {t('upload.browse')}
              </Button>
              <input
                ref={fileInputRef}
                type="file"
                multiple
                className="hidden"
                onChange={handleFileSelect}
              />
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-4">
                {t('upload.maxFileSize')}: 500MB
              </p>
            </CardContent>
          </Card>

          {/* Upload Stats */}
          {uploadFiles.length > 0 && (
            <div className="grid grid-cols-4 gap-4">
              <Card>
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-gray-600 dark:text-gray-400">{t('upload.totalFiles')}</p>
                      <p className="text-2xl font-bold text-gray-900 dark:text-white">{totalFiles}</p>
                    </div>
                    <Package className="size-8 text-blue-500 opacity-20" />
                  </div>
                </CardContent>
              </Card>
              <Card>
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-gray-600 dark:text-gray-400">{t('upload.completed')}</p>
                      <p className="text-2xl font-bold text-green-600 dark:text-green-400">{completedFiles}</p>
                    </div>
                    <CheckCircle className="size-8 text-green-500 opacity-20" />
                  </div>
                </CardContent>
              </Card>
              <Card>
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-gray-600 dark:text-gray-400">{t('upload.uploading')}</p>
                      <p className="text-2xl font-bold text-blue-600 dark:text-blue-400">{uploadingFiles}</p>
                    </div>
                    <Loader2 className="size-8 text-blue-500 opacity-20 animate-spin" />
                  </div>
                </CardContent>
              </Card>
              <Card>
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-gray-600 dark:text-gray-400">{t('upload.failed')}</p>
                      <p className="text-2xl font-bold text-red-600 dark:text-red-400">{errorFiles}</p>
                    </div>
                    <AlertCircle className="size-8 text-red-500 opacity-20" />
                  </div>
                </CardContent>
              </Card>
            </div>
          )}

          {/* File List */}
          {uploadFiles.length > 0 && (
            <Card className="flex-1 flex flex-col overflow-hidden">
              <CardHeader className="flex-row items-center justify-between space-y-0 pb-4">
                <div>
                  <CardTitle>{t('upload.selectedFiles')}</CardTitle>
                  <CardDescription>{uploadFiles.length} {t('upload.filesSelected')}</CardDescription>
                </div>
                <div className="flex items-center gap-2">
                  <Button variant="outline" size="sm" onClick={clearAll}>
                    <Trash2 className="mr-2 size-4" />
                    {t('upload.clearAll')}
                  </Button>
                  <Button 
                    onClick={handleUpload}
                    disabled={uploadingFiles > 0}
                  >
                    {uploadingFiles > 0 ? (
                      <>
                        <Loader2 className="mr-2 size-4 animate-spin" />
                        {t('upload.uploading')}...
                      </>
                    ) : (
                      <>
                        <Upload className="mr-2 size-4" />
                        {t('upload.startUpload')}
                      </>
                    )}
                  </Button>
                </div>
              </CardHeader>
              <CardContent className="flex-1 overflow-auto space-y-2">
                {uploadFiles.map((uploadFile) => (
                  <div 
                    key={uploadFile.id}
                    className="flex items-center gap-4 p-4 border border-gray-200 dark:border-gray-700 rounded-lg"
                  >
                    {getFileIcon(uploadFile.file.name)}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between mb-1">
                        <p className="font-medium text-gray-900 dark:text-white truncate">
                          {uploadFile.file.name}
                        </p>
                        <div className="flex items-center gap-2">
                          {uploadFile.status === 'pending' && (
                            <Badge variant="secondary">{t('upload.pending')}</Badge>
                          )}
                          {uploadFile.status === 'uploading' && (
                            <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300">
                              <Loader2 className="mr-1 size-3 animate-spin" />
                              {uploadFile.progress}%
                            </Badge>
                          )}
                          {uploadFile.status === 'success' && (
                            <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                              <CheckCircle className="mr-1 size-3" />
                              {t('upload.success')}
                            </Badge>
                          )}
                          {uploadFile.status === 'error' && (
                            <Badge variant="destructive">
                              <AlertCircle className="mr-1 size-3" />
                              {t('upload.failed')}
                            </Badge>
                          )}
                          <Button 
                            variant="ghost" 
                            size="sm"
                            onClick={() => removeFile(uploadFile.id)}
                            disabled={uploadFile.status === 'uploading'}
                          >
                            <X className="size-4" />
                          </Button>
                        </div>
                      </div>
                      <p className="text-sm text-gray-500 dark:text-gray-400 mb-2">
                        {formatFileSize(uploadFile.file.size)}
                      </p>
                      {uploadFile.status === 'uploading' && (
                        <Progress value={uploadFile.progress} className="h-2" />
                      )}
                      {uploadFile.error && (
                        <p className="text-sm text-red-600 dark:text-red-400 mt-1">
                          {uploadFile.error}
                        </p>
                      )}
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
          )}

          {/* Empty State */}
          {uploadFiles.length === 0 && (
            <Card className="flex-1">
              <CardContent className="flex flex-col items-center justify-center h-full py-12">
                <Package className="size-16 text-gray-300 dark:text-gray-600 mb-4" />
                <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                  {t('upload.noFilesSelected')}
                </h3>
                <p className="text-sm text-gray-600 dark:text-gray-400 text-center max-w-md">
                  {t('upload.selectFilesDesc')}
                </p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}
