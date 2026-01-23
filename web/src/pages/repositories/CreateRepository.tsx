import { useState } from 'react';
import { ArrowLeft, Database, Globe, GitBranch } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Switch } from '@/components/ui/switch';
import { Separator } from '@/components/ui/separator';
import { Checkbox } from '@/components/ui/checkbox';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';
import { useNavigate } from 'react-router-dom';


export function CreateRepository() {
  const { t } = useLanguage();
  const navigate = useNavigate();

  const onBack = () => {
    navigate('/repositories');
  };

  const [repoType, setRepoType] = useState<'hosted' | 'proxy' | 'group'>('hosted');
  const [repoName, setRepoName] = useState('');
  const [format, setFormat] = useState('maven');

  const handleCreate = () => {
    if (!repoName) {
      toast.error('Please enter a repository name');
      return;
    }
    toast.success(t('repositories.repositoryCreated'));
    onBack();
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" onClick={onBack}>
          <ArrowLeft className="size-4" />
        </Button>
        <div>
          <h1 className="text-3xl text-gray-900 dark:text-white">{t('repositories.createRepository')}</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">{t('repositories.createDialogDescription')}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Content */}
        <div className="lg:col-span-2">
          <Card>
            <CardContent className="pt-6">
              <Tabs defaultValue="basic" className="space-y-6">
                <TabsList className="grid w-full grid-cols-5">
                  <TabsTrigger value="basic">{t('repositories.general')}</TabsTrigger>
                  <TabsTrigger value="storage">{t('repositories.storage')}</TabsTrigger>
                  <TabsTrigger value="security">{t('repositories.security')}</TabsTrigger>
                  <TabsTrigger value="advanced">{t('repositories.advanced')}</TabsTrigger>
                  <TabsTrigger value="cleanup">{t('repositories.cleanup')}</TabsTrigger>
                </TabsList>
                
                {/* Basic Tab */}
                <TabsContent value="basic" className="space-y-6">
                  <div className="space-y-2">
                    <Label htmlFor="repo-type">{t('repositories.repositoryType')}</Label>
                    <Select value={repoType} onValueChange={(v: any) => setRepoType(v)}>
                      <SelectTrigger id="repo-type">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="hosted">
                          <div className="flex items-start gap-2 py-1">
                            <Database className="size-4 mt-0.5 flex-shrink-0" />
                            <div>
                              <div className="font-medium">{t('repositories.hosted')}</div>
                              <div className="text-xs text-gray-500">{t('repositories.hostedDesc')}</div>
                            </div>
                          </div>
                        </SelectItem>
                        <SelectItem value="proxy">
                          <div className="flex items-start gap-2 py-1">
                            <Globe className="size-4 mt-0.5 flex-shrink-0" />
                            <div>
                              <div className="font-medium">{t('repositories.proxy')}</div>
                              <div className="text-xs text-gray-500">{t('repositories.proxyDesc')}</div>
                            </div>
                          </div>
                        </SelectItem>
                        <SelectItem value="group">
                          <div className="flex items-start gap-2 py-1">
                            <GitBranch className="size-4 mt-0.5 flex-shrink-0" />
                            <div>
                              <div className="font-medium">{t('repositories.group')}</div>
                              <div className="text-xs text-gray-500">{t('repositories.groupDesc')}</div>
                            </div>
                          </div>
                        </SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="name">{t('repositories.repositoryName')}</Label>
                    <Input 
                      id="name" 
                      placeholder={t('repositories.namePlaceholder')} 
                      value={repoName}
                      onChange={(e) => setRepoName(e.target.value)}
                    />
                    <p className="text-xs text-gray-500">{t('repositories.nameHint')}</p>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="format">{t('repositories.repositoryFormat')}</Label>
                    <Select value={format} onValueChange={setFormat}>
                      <SelectTrigger id="format">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="maven">{t('repositories.maven')}</SelectItem>
                        <SelectItem value="docker">{t('repositories.docker')}</SelectItem>
                        <SelectItem value="npm">{t('repositories.npm')}</SelectItem>
                        <SelectItem value="nuget">{t('repositories.nuget')}</SelectItem>
                        <SelectItem value="pypi">{t('repositories.pypi')}</SelectItem>
                        <SelectItem value="apt">{t('repositories.apt')}</SelectItem>
                        <SelectItem value="yum">{t('repositories.yum')}</SelectItem>
                        <SelectItem value="raw">{t('repositories.raw')}</SelectItem>
                        <SelectItem value="helm">{t('repositories.helm')}</SelectItem>
                        <SelectItem value="go">{t('repositories.go')}</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="description">{t('common.description')}</Label>
                    <Textarea id="description" placeholder={t('repositories.descriptionPlaceholder')} rows={4} />
                  </div>

                  {repoType === 'proxy' && (
                    <div className="space-y-2">
                      <Label htmlFor="remote-url">{t('repositories.remoteUrl')}</Label>
                      <Input id="remote-url" placeholder={t('repositories.remoteUrlPlaceholder')} />
                      <p className="text-xs text-gray-500">{t('repositories.remoteUrlHint')}</p>
                    </div>
                  )}

                  {repoType === 'group' && (
                    <div className="space-y-2">
                      <Label>{t('repositories.memberReposLabel')}</Label>
                      <div className="border border-gray-200 dark:border-gray-700 rounded-lg p-4 space-y-3">
                        {['maven-releases', 'maven-central', 'maven-snapshots'].map(repo => (
                          <div key={repo} className="flex items-center gap-2">
                            <Checkbox id={`member-${repo}`} />
                            <Label htmlFor={`member-${repo}`} className="text-sm font-normal cursor-pointer">
                              {repo}
                            </Label>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </TabsContent>

                {/* Storage Tab */}
                <TabsContent value="storage" className="space-y-6">
                  <div className="space-y-2">
                    <Label htmlFor="blobstore">{t('repositories.blobStore')}</Label>
                    <Select defaultValue="default">
                      <SelectTrigger id="blobstore">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="default">{t('repositories.defaultBlobStore')}</SelectItem>
                        <SelectItem value="s3">{t('repositories.amazonS3')}</SelectItem>
                        <SelectItem value="azure">{t('repositories.azureBlob')}</SelectItem>
                        <SelectItem value="gcs">{t('repositories.googleCloud')}</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-4 p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.strictContentValidation')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.strictValidationDesc')}</p>
                      </div>
                      <Switch defaultChecked />
                    </div>

                    <Separator />

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.enableCompression')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.compressionDesc')}</p>
                      </div>
                      <Switch defaultChecked />
                    </div>

                    {repoType === 'proxy' && (
                      <>
                        <Separator />
                        <div className="flex items-center justify-between">
                          <div className="space-y-0.5">
                            <Label>{t('repositories.enableCaching')}</Label>
                            <p className="text-xs text-gray-500">{t('repositories.cachingDesc')}</p>
                          </div>
                          <Switch defaultChecked />
                        </div>
                      </>
                    )}
                  </div>

                  {repoType === 'hosted' && (
                    <div className="space-y-2">
                      <Label>{t('repositories.deploymentPolicy')}</Label>
                      <Select defaultValue="allow">
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="allow">{t('repositories.allowRedeploy')}</SelectItem>
                          <SelectItem value="disable">{t('repositories.disableRedeploy')}</SelectItem>
                          <SelectItem value="once">{t('repositories.deployOnce')}</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  )}

                  <div className="space-y-2">
                    <Label htmlFor="quota">{t('repositories.storageQuota')} (GB)</Label>
                    <Input id="quota" type="number" placeholder={t('repositories.quotaPlaceholder')} defaultValue="100" />
                    <p className="text-xs text-gray-500">{t('repositories.quotaHint')}</p>
                  </div>
                </TabsContent>

                {/* Security Tab */}
                <TabsContent value="security" className="space-y-6">
                  <div className="space-y-4 p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.publicRepository')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.publicAccessDesc')}</p>
                      </div>
                      <Switch />
                    </div>

                    <Separator />

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.enableIndexing')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.indexingDesc')}</p>
                      </div>
                      <Switch defaultChecked />
                    </div>

                    <Separator />

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.securityScanning')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.scanningDesc')}</p>
                      </div>
                      <Switch defaultChecked />
                    </div>

                    <Separator />

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.licenseAnalysis')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.licenseAnalysisDesc')}</p>
                      </div>
                      <Switch />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label>{t('repositories.accessControl')}</Label>
                    <Select defaultValue="private">
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="private">{t('repositories.accessControlOptions.private')}</SelectItem>
                        <SelectItem value="public-read">{t('repositories.accessControlOptions.publicRead')}</SelectItem>
                        <SelectItem value="public">{t('repositories.accessControlOptions.public')}</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="allowed-ips">{t('repositories.allowedIps')}</Label>
                    <Textarea 
                      id="allowed-ips" 
                      placeholder={t('repositories.allowedIpsPlaceholder')}
                      rows={3}
                    />
                    <p className="text-xs text-gray-500">{t('repositories.allowedIpsHint')}</p>
                  </div>
                </TabsContent>

                {/* Advanced Tab */}
                <TabsContent value="advanced" className="space-y-6">
                  {repoType === 'proxy' && (
                    <>
                      <div className="space-y-2">
                        <Label>{t('repositories.authType')}</Label>
                        <Select defaultValue="none">
                          <SelectTrigger>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="none">{t('repositories.noAuth')}</SelectItem>
                            <SelectItem value="basic">{t('repositories.basicAuth')}</SelectItem>
                            <SelectItem value="token">{t('repositories.bearerToken')}</SelectItem>
                            <SelectItem value="ntlm">{t('repositories.ntlmAuth')}</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>

                      <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <Label htmlFor="username">{t('repositories.username')}</Label>
                          <Input id="username" placeholder={t('repositories.usernamePlaceholder')} />
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="password">{t('repositories.password')}</Label>
                          <Input id="password" type="password" placeholder={t('repositories.passwordPlaceholder')} />
                        </div>
                      </div>

                      <Separator />

                      <div className="space-y-2">
                        <Label htmlFor="cache-ttl">{t('repositories.cacheTtl')}</Label>
                        <Input id="cache-ttl" type="number" placeholder="24" defaultValue="24" />
                        <p className="text-xs text-gray-500">{t('repositories.cacheTtlHint')}</p>
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="max-age">{t('repositories.maxComponentAge')}</Label>
                        <Input id="max-age" type="number" placeholder="365" defaultValue="365" />
                        <p className="text-xs text-gray-500">{t('repositories.maxAgeHint')}</p>
                      </div>

                      <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                        <div className="space-y-0.5">
                          <Label>{t('repositories.blockedOutbound')}</Label>
                          <p className="text-xs text-gray-500">{t('repositories.blockOutboundDesc')}</p>
                        </div>
                        <Switch />
                      </div>
                    </>
                  )}

                  <div className="space-y-2">
                    <Label htmlFor="http-port">{t('repositories.httpPort')}</Label>
                    <Input id="http-port" type="number" placeholder={t('repositories.httpPortPlaceholder')} />
                    <p className="text-xs text-gray-500">{t('repositories.httpPortHint')}</p>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="custom-url">{t('repositories.customUrl')}</Label>
                    <Input id="custom-url" placeholder={t('repositories.customUrlPlaceholder')} />
                    <p className="text-xs text-gray-500">{t('repositories.customUrlHint')}</p>
                  </div>

                  <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="space-y-0.5">
                      <Label>{t('repositories.enableWebhooks')}</Label>
                      <p className="text-xs text-gray-500">{t('repositories.webhooksDesc')}</p>
                    </div>
                    <Switch />
                  </div>
                </TabsContent>

                {/* Cleanup Tab */}
                <TabsContent value="cleanup" className="space-y-6">
                  <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="space-y-0.5">
                      <Label>{t('repositories.enableCleanup')}</Label>
                      <p className="text-xs text-gray-500">{t('repositories.cleanupDesc')}</p>
                    </div>
                    <Switch />
                  </div>

                  <div className="space-y-2">
                    <Label>{t('repositories.cleanupPolicy')}</Label>
                    <Select defaultValue="none">
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="none">{t('repositories.noCleanupPolicy')}</SelectItem>
                        <SelectItem value="age">{t('repositories.deleteOlderThanDays')}</SelectItem>
                        <SelectItem value="count">{t('repositories.keepLastVersions')}</SelectItem>
                        <SelectItem value="size">{t('repositories.deleteBySizePolicy')}</SelectItem>
                        <SelectItem value="custom">{t('repositories.customPolicy')}</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="cleanup-days">{t('repositories.deleteNotDownloadedDays')}</Label>
                    <Input id="cleanup-days" type="number" placeholder="90" defaultValue="90" />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="keep-versions">{t('repositories.keepLastVersions')}</Label>
                    <Input id="keep-versions" type="number" placeholder="5" defaultValue="5" />
                    <p className="text-xs text-gray-500">{t('repositories.keepVersionsHint')}</p>
                  </div>

                  <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="space-y-0.5">
                      <Label>{t('repositories.deleteSnapshots')}</Label>
                      <p className="text-xs text-gray-500">{t('repositories.deleteSnapshotsDesc')}</p>
                    </div>
                    <Switch />
                  </div>
                </TabsContent>
              </Tabs>
            </CardContent>
          </Card>
        </div>

        {/* Sidebar - Quick Info */}
        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Repository Type</CardTitle>
              <CardDescription>
                {repoType === 'hosted' && t('repositories.hostedDesc')}
                {repoType === 'proxy' && t('repositories.proxyDesc')}
                {repoType === 'group' && t('repositories.groupDesc')}
              </CardDescription>
            </CardHeader>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-base">Format: {format.toUpperCase()}</CardTitle>
              <CardDescription>
                {format === 'maven' && 'For Java projects using Maven or Gradle'}
                {format === 'docker' && 'For Docker container images'}
                {format === 'npm' && 'For Node.js packages'}
                {format === 'pypi' && 'For Python packages'}
                {format === 'nuget' && 'For .NET packages'}
                {format === 'apt' && 'For Debian/Ubuntu packages'}
                {format === 'yum' && 'For RedHat/CentOS packages'}
                {format === 'raw' && 'For any file type'}
                {format === 'helm' && 'For Kubernetes Helm charts'}
                {format === 'go' && 'For Go modules'}
              </CardDescription>
            </CardHeader>
          </Card>
        </div>
      </div>

      {/* Footer Actions */}
      <div className="flex items-center justify-between border-t border-gray-200 dark:border-gray-700 pt-6">
        <Button variant="outline" onClick={onBack}>
          {t('common.cancel')}
        </Button>
        <div className="flex items-center gap-2">
          <Button variant="outline">
            {t('common.save')} as Draft
          </Button>
          <Button onClick={handleCreate}>
            {t('common.create')} {t('repositories.title')}
          </Button>
        </div>
      </div>
    </div>
  );
}
