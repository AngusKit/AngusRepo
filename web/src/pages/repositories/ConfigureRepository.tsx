import { useState } from 'react';
import { ArrowLeft, Database, Globe, GitBranch, Save } from 'lucide-react';
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
import { Badge } from '@/components/ui/badge';
import { useNavigate, useParams } from 'react-router-dom';


export function ConfigureRepository() {
  const { t } = useLanguage();
  const navigate = useNavigate();
  const { repositoryId } = useParams();

  const onBack = () => {
    navigate(`/repositories/detail/${repositoryId}`);
  };

  // Mock data - in real app, fetch by repositoryId
  const [repository] = useState({
    id: repositoryId,
    name: 'maven-releases',
    format: 'Maven',
    type: 'hosted' as const,
    description: 'Maven release artifacts repository for production builds',
    settings: {
      public: false,
      indexed: true,
      compressionEnabled: true,
      cachingEnabled: false,
      deploymentPolicy: 'allow',
      blobStore: 'default',
      strictValidation: true,
      securityScanning: true,
      licenseAnalysis: false,
      quota: 100,
    },
    proxy: {
      authType: 'none',
      username: '',
      password: '',
      cacheTtl: 24,
      maxAge: 365,
      blockOutbound: false,
    },
    cleanup: {
      enabled: false,
      policy: 'none',
      notDownloadedDays: 90,
      keepVersions: 5,
      deleteSnapshots: false,
    },
  });

  const [publicAccess, setPublicAccess] = useState(repository.settings.public);
  const [indexed, setIndexed] = useState(repository.settings.indexed);
  const [compression, setCompression] = useState(repository.settings.compressionEnabled);
  const [scanning, setScanning] = useState(repository.settings.securityScanning);
  const [licenseAnalysis, setLicenseAnalysis] = useState(repository.settings.licenseAnalysis);

  const handleSave = () => {
    toast.success(t('repositories.changesSaved'));
  };

  const handleSaveAndBack = () => {
    toast.success(t('repositories.changesSaved'));
    onBack();
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm" onClick={onBack}>
            <ArrowLeft className="size-4" />
          </Button>
          <div>
            <h1 className="text-3xl text-gray-900 dark:text-white">{t('repositories.configure')}: {repository.name}</h1>
            <p className="text-gray-600 dark:text-gray-400 mt-1">{t('repositories.editDialogDescription')}</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Badge variant="outline" className="px-3 py-1">{repository.format}</Badge>
          <Badge variant="outline" className="px-3 py-1">{repository.type}</Badge>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Content */}
        <div className="lg:col-span-2">
          <Card>
            <CardContent className="pt-6">
              <Tabs defaultValue="general" className="space-y-6">
                <TabsList className="grid w-full grid-cols-5">
                  <TabsTrigger value="general">{t('repositories.generalTab')}</TabsTrigger>
                  <TabsTrigger value="storage">{t('repositories.storage')}</TabsTrigger>
                  <TabsTrigger value="security">{t('repositories.securityTab')}</TabsTrigger>
                  <TabsTrigger value="advanced">{t('repositories.advanced')}</TabsTrigger>
                  <TabsTrigger value="cleanup">{t('repositories.cleanupTab')}</TabsTrigger>
                </TabsList>
                
                {/* General Tab */}
                <TabsContent value="general" className="space-y-6">
                  <div className="space-y-2">
                    <Label htmlFor="name">{t('repositories.repositoryName')}</Label>
                    <Input id="name" defaultValue={repository.name} disabled />
                    <p className="text-xs text-gray-500">Repository name cannot be changed</p>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="format">{t('repositories.repositoryFormat')}</Label>
                    <Input id="format" defaultValue={repository.format} disabled />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="type">{t('repositories.repositoryType')}</Label>
                    <Input id="type" defaultValue={repository.type} disabled className="capitalize" />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="description">{t('common.description')}</Label>
                    <Textarea id="description" defaultValue={repository.description} rows={4} />
                  </div>

                  <div className="space-y-4 p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.publicRepository')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.publicAccessDesc')}</p>
                      </div>
                      <Switch checked={publicAccess} onCheckedChange={setPublicAccess} />
                    </div>

                    <Separator />

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.enableIndexing')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.indexingDesc')}</p>
                      </div>
                      <Switch checked={indexed} onCheckedChange={setIndexed} />
                    </div>
                  </div>
                </TabsContent>

                {/* Storage Tab */}
                <TabsContent value="storage" className="space-y-6">
                  <div className="space-y-2">
                    <Label htmlFor="blobstore">{t('repositories.blobStore')}</Label>
                    <Select defaultValue={repository.settings.blobStore}>
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
                    <p className="text-xs text-gray-500">Changing blob store requires repository rebuild</p>
                  </div>

                  <div className="space-y-4 p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.strictContentValidation')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.strictValidationDesc')}</p>
                      </div>
                      <Switch defaultChecked={repository.settings.strictValidation} />
                    </div>

                    <Separator />

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.enableCompression')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.compressionDesc')}</p>
                      </div>
                      <Switch checked={compression} onCheckedChange={setCompression} />
                    </div>

                    {repository.type === 'proxy' && (
                      <>
                        <Separator />
                        <div className="flex items-center justify-between">
                          <div className="space-y-0.5">
                            <Label>{t('repositories.enableCaching')}</Label>
                            <p className="text-xs text-gray-500">{t('repositories.cachingDesc')}</p>
                          </div>
                          <Switch defaultChecked={repository.settings.cachingEnabled} />
                        </div>
                      </>
                    )}
                  </div>

                  {repository.type === 'hosted' && (
                    <div className="space-y-2">
                      <Label>{t('repositories.deploymentPolicy')}</Label>
                      <Select defaultValue={repository.settings.deploymentPolicy}>
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
                    <Input id="quota" type="number" defaultValue={repository.settings.quota} />
                    <p className="text-xs text-gray-500">{t('repositories.quotaHint')}</p>
                  </div>
                </TabsContent>

                {/* Security Tab */}
                <TabsContent value="security" className="space-y-6">
                  <div className="space-y-4 p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.securityScanning')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.scanningDesc2')}</p>
                      </div>
                      <Switch checked={scanning} onCheckedChange={setScanning} />
                    </div>

                    <Separator />

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>{t('repositories.licenseAnalysis')}</Label>
                        <p className="text-xs text-gray-500">{t('repositories.licenseAnalysisDesc')}</p>
                      </div>
                      <Switch checked={licenseAnalysis} onCheckedChange={setLicenseAnalysis} />
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
                      rows={4}
                    />
                    <p className="text-xs text-gray-500">{t('repositories.allowedIpsHint')}</p>
                  </div>
                </TabsContent>

                {/* Advanced Tab */}
                <TabsContent value="advanced" className="space-y-6">
                  {repository.type === 'proxy' && (
                    <>
                      <div className="space-y-2">
                        <Label>{t('repositories.authType')}</Label>
                        <Select defaultValue={repository.proxy.authType}>
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
                          <Input id="username" defaultValue={repository.proxy.username} />
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="password">{t('repositories.password')}</Label>
                          <Input id="password" type="password" placeholder="••••••••" />
                        </div>
                      </div>

                      <Separator />

                      <div className="space-y-2">
                        <Label htmlFor="cache-ttl">{t('repositories.cacheTtl')} (hours)</Label>
                        <Input id="cache-ttl" type="number" defaultValue={repository.proxy.cacheTtl} />
                        <p className="text-xs text-gray-500">{t('repositories.cacheTtlHint')}</p>
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="max-age">{t('repositories.maxComponentAge')} (days)</Label>
                        <Input id="max-age" type="number" defaultValue={repository.proxy.maxAge} />
                        <p className="text-xs text-gray-500">{t('repositories.maxAgeHint')}</p>
                      </div>

                      <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                        <div className="space-y-0.5">
                          <Label>{t('repositories.blockedOutbound')}</Label>
                          <p className="text-xs text-gray-500">{t('repositories.blockOutboundDesc')}</p>
                        </div>
                        <Switch defaultChecked={repository.proxy.blockOutbound} />
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
                    <Switch defaultChecked={repository.cleanup.enabled} />
                  </div>

                  <div className="space-y-2">
                    <Label>{t('repositories.cleanupPolicy')}</Label>
                    <Select defaultValue={repository.cleanup.policy}>
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
                    <Input 
                      id="cleanup-days" 
                      type="number" 
                      defaultValue={repository.cleanup.notDownloadedDays} 
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="keep-versions">{t('repositories.keepLastVersions')}</Label>
                    <Input 
                      id="keep-versions" 
                      type="number" 
                      defaultValue={repository.cleanup.keepVersions} 
                    />
                    <p className="text-xs text-gray-500">{t('repositories.keepVersionsHint')}</p>
                  </div>

                  <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="space-y-0.5">
                      <Label>{t('repositories.deleteSnapshots')}</Label>
                      <p className="text-xs text-gray-500">{t('repositories.deleteSnapshotsDesc')}</p>
                    </div>
                    <Switch defaultChecked={repository.cleanup.deleteSnapshots} />
                  </div>
                </TabsContent>
              </Tabs>
            </CardContent>
          </Card>
        </div>

        {/* Sidebar - Current Settings */}
        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">Active Settings</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-sm">
              <div className="flex items-center justify-between">
                <span className="text-gray-600 dark:text-gray-400">Public Access</span>
                <Badge variant={publicAccess ? 'default' : 'secondary'}>
                  {publicAccess ? 'Yes' : 'No'}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-600 dark:text-gray-400">Indexed</span>
                <Badge variant={indexed ? 'default' : 'secondary'}>
                  {indexed ? 'Yes' : 'No'}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-600 dark:text-gray-400">Compression</span>
                <Badge variant={compression ? 'default' : 'secondary'}>
                  {compression ? 'Yes' : 'No'}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-600 dark:text-gray-400">Security Scan</span>
                <Badge variant={scanning ? 'default' : 'secondary'}>
                  {scanning ? 'Yes' : 'No'}
                </Badge>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-base">Quick Actions</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <Button variant="outline" size="sm" className="w-full justify-start">
                Rebuild Index
              </Button>
              <Button variant="outline" size="sm" className="w-full justify-start">
                Clear Cache
              </Button>
              <Button variant="outline" size="sm" className="w-full justify-start">
                Export Configuration
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Footer Actions */}
      <div className="flex items-center justify-between border-t border-gray-200 dark:border-gray-700 pt-6">
        <Button variant="outline" onClick={onBack}>
          {t('common.cancel')}
        </Button>
        <div className="flex items-center gap-2">
          <Button variant="outline" onClick={handleSave}>
            <Save className="mr-2 size-4" />
            {t('common.save')}
          </Button>
          <Button onClick={handleSaveAndBack}>
            {t('repositories.saveChanges')} & {t('repositories.close')}
          </Button>
        </div>
      </div>
    </div>
  );
}
