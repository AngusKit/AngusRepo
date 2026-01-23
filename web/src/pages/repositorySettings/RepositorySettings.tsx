import { useState } from 'react';
import { Database, HardDrive, Globe, Lock, Webhook, Save, Plus, Trash2, Edit, AlertTriangle } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';

export function RepositorySettings() {
  const { t } = useLanguage();
  
  // General Settings
  const [defaultRepository, setDefaultRepository] = useState('maven-central');
  const [anonymousAccess, setAnonymousAccess] = useState(false);
  const [indexingEnabled, setIndexingEnabled] = useState(true);
  const [compressionEnabled, setCompressionEnabled] = useState(true);

  // Storage Settings
  const [storageQuota, setStorageQuota] = useState('500');
  const [retentionDays, setRetentionDays] = useState('90');
  const [autoCleanup, setAutoCleanup] = useState(true);
  const [deduplicationEnabled, setDeduplicationEnabled] = useState(true);

  // Webhooks
  const [webhooks, setWebhooks] = useState([
    {
      id: '1',
      name: 'Slack Notifications',
      url: 'https://hooks.slack.com/services/T00000000/B00000000/XXXX',
      events: ['artifact.upload', 'artifact.download'],
      active: true,
      lastTrigger: '2 hours ago',
    },
    {
      id: '2',
      name: 'CI/CD Pipeline',
      url: 'https://jenkins.company.com/webhook/angusrepo',
      events: ['artifact.upload', 'scan.complete'],
      active: true,
      lastTrigger: '1 day ago',
    },
    {
      id: '3',
      name: 'Security Alerts',
      url: 'https://security.company.com/webhook',
      events: ['vulnerability.found'],
      active: false,
      lastTrigger: 'Never',
    },
  ]);

  const [showCreateWebhook, setShowCreateWebhook] = useState(false);
  const [newWebhookName, setNewWebhookName] = useState('');
  const [newWebhookUrl, setNewWebhookUrl] = useState('');

  const handleSaveGeneral = () => {
    toast.success(t('settings.settingsSaved'));
  };

  const handleSaveStorage = () => {
    toast.success(t('settings.settingsSaved'));
  };

  const handleCreateWebhook = () => {
    if (!newWebhookName.trim() || !newWebhookUrl.trim()) {
      toast.error(t('settings.pleaseFillAllFields'));
      return;
    }
    const newWebhook = {
      id: String(webhooks.length + 1),
      name: newWebhookName,
      url: newWebhookUrl,
      events: ['artifact.upload'],
      active: true,
      lastTrigger: 'Never',
    };
    setWebhooks([...webhooks, newWebhook]);
    setNewWebhookName('');
    setNewWebhookUrl('');
    setShowCreateWebhook(false);
    toast.success(t('settings.tokenCreated'));
  };

  const handleDeleteWebhook = (id: string, name: string) => {
    setWebhooks(webhooks.filter(w => w.id !== id));
    toast.success(t('settings.webhookDeleted'));
  };

  const handleToggleWebhook = (id: string) => {
    setWebhooks(webhooks.map(w => 
      w.id === id ? { ...w, active: !w.active } : w
    ));
    toast.success(t('settings.webhookStatusUpdated'));
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 dark:text-white">{t('settings.repository')}</h1>
        <p className="text-gray-600 dark:text-gray-400 mt-1">{t('settings.configureDefaults')}</p>
      </div>

      <Tabs defaultValue="general" className="space-y-6">
        <TabsList>
          <TabsTrigger value="general">
            <Database className="mr-2 size-4" />
            {t('settings.general')}
          </TabsTrigger>
          <TabsTrigger value="storage">
            <HardDrive className="mr-2 size-4" />
            {t('settings.storage')}
          </TabsTrigger>
          <TabsTrigger value="permissions">
            <Lock className="mr-2 size-4" />
            {t('settings.permissions')}
          </TabsTrigger>
          <TabsTrigger value="webhooks">
            <Webhook className="mr-2 size-4" />
            {t('settings.webhooks')}
          </TabsTrigger>
        </TabsList>

        {/* General Settings Tab */}
        <TabsContent value="general" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.defaultRepository')}</CardTitle>
              <CardDescription>{t('settings.setDefaultRepository')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="defaultRepo">{t('settings.defaultRepository')}</Label>
                <select
                  id="defaultRepo"
                  value={defaultRepository}
                  onChange={(e) => setDefaultRepository(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
                >
                  <option value="maven-central">maven-central</option>
                  <option value="docker-hub-proxy">docker-hub-proxy</option>
                  <option value="npm-public">npm-public</option>
                  <option value="nuget-gallery">nuget-gallery</option>
                  <option value="pypi-proxy">pypi-proxy</option>
                </select>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.accessControl')}</CardTitle>
              <CardDescription>{t('settings.configureAccessPolicies')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.anonymousAccess')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.allowAnonymousAccess')}
                  </p>
                </div>
                <Switch
                  checked={anonymousAccess}
                  onCheckedChange={setAnonymousAccess}
                />
              </div>

              {anonymousAccess && (
                <div className="p-4 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800 rounded-lg flex items-start gap-3">
                  <AlertTriangle className="size-5 text-yellow-600 dark:text-yellow-400 flex-shrink-0 mt-0.5" />
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.securityWarning')}</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                      {t('settings.anonymousAccessWarning')}
                    </p>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.repositoryFeatures')}</CardTitle>
              <CardDescription>{t('settings.enableDisableFeatures')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.indexing')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.enableSearchIndexing')}
                  </p>
                </div>
                <Switch
                  checked={indexingEnabled}
                  onCheckedChange={setIndexingEnabled}
                />
              </div>

              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.compression')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.compressArtifacts')}
                  </p>
                </div>
                <Switch
                  checked={compressionEnabled}
                  onCheckedChange={setCompressionEnabled}
                />
              </div>
            </CardContent>
          </Card>

          <div className="flex justify-end">
            <Button onClick={handleSaveGeneral}>
              <Save className="mr-2 size-4" />
              {t('settings.saveChanges')}
            </Button>
          </div>
        </TabsContent>

        {/* Storage Settings Tab */}
        <TabsContent value="storage" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.storageQuota')}</CardTitle>
              <CardDescription>{t('settings.configureStorageLimits')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="storageQuota">{t('settings.storageQuotaGB')}</Label>
                <Input
                  id="storageQuota"
                  type="number"
                  value={storageQuota}
                  onChange={(e) => setStorageQuota(e.target.value)}
                  placeholder={t('settings.enterStorageQuota')}
                />
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  {t('settings.currentUsage')}: 287 GB / {storageQuota} GB (57%)
                </p>
              </div>

              <div className="mt-4">
                <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                  <div
                    className="bg-blue-600 h-2 rounded-full"
                    style={{ width: '57%' }}
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.retentionPolicy')}</CardTitle>
              <CardDescription>{t('settings.configureRetention')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="retentionDays">{t('settings.retentionPeriodDays')}</Label>
                <Input
                  id="retentionDays"
                  type="number"
                  value={retentionDays}
                  onChange={(e) => setRetentionDays(e.target.value)}
                  placeholder={t('settings.enterRetentionPeriod')}
                />
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  {t('settings.artifactsAutoDeleted').replace('天', retentionDays + (t('common.language') === 'zh-CN' ? '天' : ' days'))}
                </p>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.storageOptimization')}</CardTitle>
              <CardDescription>{t('settings.enableOptimization')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.autoCleanup')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.autoRemoveOld')}
                  </p>
                </div>
                <Switch
                  checked={autoCleanup}
                  onCheckedChange={setAutoCleanup}
                />
              </div>

              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.deduplication')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.useDeduplication')}
                  </p>
                </div>
                <Switch
                  checked={deduplicationEnabled}
                  onCheckedChange={setDeduplicationEnabled}
                />
              </div>
            </CardContent>
          </Card>

          <div className="flex justify-end">
            <Button onClick={handleSaveStorage}>
              <Save className="mr-2 size-4" />
              {t('settings.saveChanges')}
            </Button>
          </div>
        </TabsContent>

        {/* Permissions Tab */}
        <TabsContent value="permissions" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.defaultPermissions')}</CardTitle>
              <CardDescription>{t('settings.setDefaultPermissions')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.administrators')}</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                      {t('settings.fullControlRepositories')}
                    </p>
                  </div>
                  <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                    {t('settings.fullAccess')}
                  </Badge>
                </div>

                <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.developers')}</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                      {t('settings.canUploadDownload')}
                    </p>
                  </div>
                  <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300">
                    {t('settings.readWrite')}
                  </Badge>
                </div>

                <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.guests')}</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                      {t('settings.viewDownloadOnly')}
                    </p>
                  </div>
                  <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300">
                    {t('settings.readOnly')}
                  </Badge>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.ipWhitelist')}</CardTitle>
              <CardDescription>{t('settings.restrictByIP')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-2">
                <Input placeholder={t('settings.enterIPAddress')} className="flex-1" />
                <Button>
                  <Plus className="mr-2 size-4" />
                  {t('settings.add')}
                </Button>
              </div>

              <div className="space-y-2">
                {[
                  { ip: '192.168.1.0/24', description: t('settings.officeNetwork'), added: '2024-01-15' },
                  { ip: '10.0.0.0/8', description: t('settings.vpnNetwork'), added: '2024-01-10' },
                ].map((item, index) => (
                  <div key={index} className="flex items-center justify-between p-3 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div>
                      <p className="font-medium text-gray-900 dark:text-white">{item.ip}</p>
                      <p className="text-sm text-gray-600 dark:text-gray-400">{item.description} · {t('settings.added')} {item.added}</p>
                    </div>
                    <Button variant="ghost" size="sm">
                      <Trash2 className="size-4 text-red-500" />
                    </Button>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Webhooks Tab */}
        <TabsContent value="webhooks" className="space-y-6">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>{t('settings.webhooks')}</CardTitle>
                  <CardDescription>{t('settings.configureWebhooks')}</CardDescription>
                </div>
                <Button onClick={() => setShowCreateWebhook(true)}>
                  <Plus className="mr-2 size-4" />
                  {t('settings.addWebhook')}
                </Button>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {showCreateWebhook && (
                <div className="p-4 border-2 border-dashed border-blue-300 dark:border-blue-700 rounded-lg bg-blue-50 dark:bg-blue-900/10 space-y-4">
                  <h3 className="font-medium text-gray-900 dark:text-white">{t('settings.createNewWebhook')}</h3>
                  <div className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="webhookName">{t('settings.webhookName')}</Label>
                      <Input
                        id="webhookName"
                        value={newWebhookName}
                        onChange={(e) => setNewWebhookName(e.target.value)}
                        placeholder={t('settings.webhookNamePlaceholder')}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="webhookUrl">{t('settings.webhookUrl')}</Label>
                      <Input
                        id="webhookUrl"
                        value={newWebhookUrl}
                        onChange={(e) => setNewWebhookUrl(e.target.value)}
                        placeholder={t('settings.webhookUrlPlaceholder')}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label>{t('settings.events')}</Label>
                      <div className="space-y-2">
                        <label className="flex items-center gap-2">
                          <input type="checkbox" defaultChecked className="rounded" />
                          <span className="text-sm text-gray-700 dark:text-gray-300">{t('settings.artifactUpload')}</span>
                        </label>
                        <label className="flex items-center gap-2">
                          <input type="checkbox" className="rounded" />
                          <span className="text-sm text-gray-700 dark:text-gray-300">{t('settings.artifactDownload')}</span>
                        </label>
                        <label className="flex items-center gap-2">
                          <input type="checkbox" className="rounded" />
                          <span className="text-sm text-gray-700 dark:text-gray-300">{t('settings.securityScanComplete')}</span>
                        </label>
                        <label className="flex items-center gap-2">
                          <input type="checkbox" className="rounded" />
                          <span className="text-sm text-gray-700 dark:text-gray-300">{t('settings.vulnerabilityFound')}</span>
                        </label>
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <Button onClick={handleCreateWebhook}>{t('settings.createWebhook')}</Button>
                      <Button variant="ghost" onClick={() => {
                        setShowCreateWebhook(false);
                        setNewWebhookName('');
                        setNewWebhookUrl('');
                      }}>
                        {t('settings.cancel')}
                      </Button>
                    </div>
                  </div>
                </div>
              )}

              {webhooks.map((webhook) => (
                <div key={webhook.id} className="p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <h4 className="font-medium text-gray-900 dark:text-white">{webhook.name}</h4>
                        <Badge
                          className={
                            webhook.active
                              ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300'
                              : 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300'
                          }
                        >
                          {webhook.active ? t('settings.active') : t('settings.inactive')}
                        </Badge>
                      </div>
                      <p className="text-sm text-gray-600 dark:text-gray-400 break-all">
                        {webhook.url}
                      </p>
                    </div>
                    <div className="flex items-center gap-2 ml-4">
                      <Button variant="ghost" size="sm">
                        <Edit className="size-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDeleteWebhook(webhook.id, webhook.name)}
                      >
                        <Trash2 className="size-4 text-red-500" />
                      </Button>
                    </div>
                  </div>
                  <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                    <span>{t('settings.lastTrigger')}: {webhook.lastTrigger}</span>
                    <span>•</span>
                    <span>{t('settings.events')}: {webhook.events.length}</span>
                  </div>
                  <div className="flex gap-2 mt-3">
                    {webhook.events.map((event, idx) => (
                      <Badge key={idx} variant="outline" className="text-xs">
                        {event}
                      </Badge>
                    ))}
                  </div>
                  <div className="flex gap-2 mt-3">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleToggleWebhook(webhook.id)}
                    >
                      {webhook.active ? t('settings.disable') : t('settings.enable')}
                    </Button>
                    <Button variant="outline" size="sm">
                      {t('settings.testWebhook')}
                    </Button>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}