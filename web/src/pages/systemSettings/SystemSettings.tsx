import { useState } from 'react';
import { Settings, Server, Lock, Plug, Mail, Globe, Database, Save, AlertCircle, CheckCircle } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';

export function SystemSettings() {
  const { t } = useLanguage();

  // General Settings
  const [systemName, setSystemName] = useState('AngusRepo');
  const [systemUrl, setSystemUrl] = useState('https://repo.company.com');
  const [maintenanceMode, setMaintenanceMode] = useState(false);
  const [debugMode, setDebugMode] = useState(false);

  // Storage Settings
  const [storageBackend, setStorageBackend] = useState('local');
  const [s3Bucket, setS3Bucket] = useState('angusrepo-artifacts');
  const [s3Region, setS3Region] = useState('us-east-1');
  const [backupEnabled, setBackupEnabled] = useState(true);
  const [backupSchedule, setBackupSchedule] = useState('daily');

  // Authentication Settings
  const [ldapEnabled, setLdapEnabled] = useState(false);
  const [ldapServer, setLdapServer] = useState('ldap://ldap.company.com');
  const [samlEnabled, setSamlEnabled] = useState(true);
  const [samlEntityId, setSamlEntityId] = useState('https://repo.company.com/saml');
  const [passwordPolicy, setPasswordPolicy] = useState({
    minLength: 8,
    requireUppercase: true,
    requireNumbers: true,
    requireSymbols: true,
    expiryDays: 90,
  });

  // Integration Settings
  const [jenkinsEnabled, setJenkinsEnabled] = useState(true);
  const [gitlabEnabled, setGitlabEnabled] = useState(true);
  const [jiraEnabled, setJiraEnabled] = useState(false);
  const [slackEnabled, setSlackEnabled] = useState(true);

  const handleSaveGeneral = () => {
    toast.success(t('settings.settingsSaved'));
  };

  const handleTestConnection = (type?: string) => {
    toast.success(`${type || ''} ${t('settings.connectionSuccessful')}`);
  };

  const handleSaveAuth = () => {
    toast.success(t('settings.settingsSaved'));
  };

  const handleSaveIntegration = () => {
    toast.success(t('settings.settingsSaved'));
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 dark:text-white">{t('settings.system')}</h1>
        <p className="text-gray-600 dark:text-gray-400 mt-1">{t('settings.configureSystemWide')}</p>
      </div>

      {/* System Status */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('settings.systemStatus')}</p>
                <p className="text-xl font-semibold text-green-600 dark:text-green-400 mt-1">
                  {t('settings.healthy')}
                </p>
              </div>
              <div className="size-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                <CheckCircle className="size-6 text-green-600 dark:text-green-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('settings.version')}</p>
                <p className="text-xl font-semibold text-gray-900 dark:text-white mt-1">
                  v2.5.0
                </p>
              </div>
              <div className="size-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                <Server className="size-6 text-blue-600 dark:text-blue-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('settings.uptime')}</p>
                <p className="text-xl font-semibold text-gray-900 dark:text-white mt-1">
                  45 {t('settings.days')}
                </p>
              </div>
              <div className="size-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                <Database className="size-6 text-purple-600 dark:text-purple-400" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="general" className="space-y-6">
        <TabsList>
          <TabsTrigger value="general">
            <Settings className="mr-2 size-4" />
            {t('settings.general')}
          </TabsTrigger>
          <TabsTrigger value="storage">
            <Database className="mr-2 size-4" />
            {t('settings.storage')}
          </TabsTrigger>
          <TabsTrigger value="authentication">
            <Lock className="mr-2 size-4" />
            {t('settings.authentication')}
          </TabsTrigger>
          <TabsTrigger value="integration">
            <Plug className="mr-2 size-4" />
            {t('settings.integration')}
          </TabsTrigger>
        </TabsList>

        {/* General Tab */}
        <TabsContent value="general" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.systemInformation')}</CardTitle>
              <CardDescription>{t('settings.configureBasicSystem')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="systemName">{t('settings.systemName')}</Label>
                <Input
                  id="systemName"
                  value={systemName}
                  onChange={(e) => setSystemName(e.target.value)}
                  placeholder={t('settings.enterSystemName')}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="systemUrl">{t('settings.systemUrl')}</Label>
                <Input
                  id="systemUrl"
                  value={systemUrl}
                  onChange={(e) => setSystemUrl(e.target.value)}
                  placeholder="https://repo.company.com"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="adminEmail">{t('settings.adminEmail')}</Label>
                <Input
                  id="adminEmail"
                  type="email"
                  defaultValue="admin@company.com"
                  placeholder="admin@company.com"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="timezone">{t('settings.timezone')}</Label>
                <select
                  id="timezone"
                  className="w-full px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
                >
                  <option value="utc">UTC</option>
                  <option value="pst">Pacific Time (PST)</option>
                  <option value="est">Eastern Time (EST)</option>
                  <option value="cet">Central European Time (CET)</option>
                  <option value="jst">Japan Standard Time (JST)</option>
                </select>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.systemMode')}</CardTitle>
              <CardDescription>{t('settings.controlSystemModes')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.maintenanceMode')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.maintenanceModeDesc')}
                  </p>
                </div>
                <Switch
                  checked={maintenanceMode}
                  onCheckedChange={setMaintenanceMode}
                />
              </div>

              {maintenanceMode && (
                <div className="p-4 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800 rounded-lg flex items-start gap-3">
                  <AlertCircle className="size-5 text-yellow-600 dark:text-yellow-400 flex-shrink-0 mt-0.5" />
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.warning')}</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                      {t('settings.maintenanceModeWarning')}
                    </p>
                  </div>
                </div>
              )}

              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.debugMode')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.enableDetailedLogging')}
                  </p>
                </div>
                <Switch
                  checked={debugMode}
                  onCheckedChange={setDebugMode}
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

        {/* Storage Tab */}
        <TabsContent value="storage" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.storageBackend')}</CardTitle>
              <CardDescription>{t('settings.configureStorageBackend')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="storageBackend">{t('settings.storageType')}</Label>
                <select
                  id="storageBackend"
                  value={storageBackend}
                  onChange={(e) => setStorageBackend(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
                >
                  <option value="local">{t('settings.localFileSystem')}</option>
                  <option value="s3">Amazon S3</option>
                  <option value="azure">Azure Blob Storage</option>
                  <option value="gcs">Google Cloud Storage</option>
                </select>
              </div>

              {storageBackend === 's3' && (
                <>
                  <div className="space-y-2">
                    <Label htmlFor="s3Bucket">{t('settings.s3BucketName')}</Label>
                    <Input
                      id="s3Bucket"
                      value={s3Bucket}
                      onChange={(e) => setS3Bucket(e.target.value)}
                      placeholder="my-bucket-name"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="s3Region">{t('settings.awsRegion')}</Label>
                    <Input
                      id="s3Region"
                      value={s3Region}
                      onChange={(e) => setS3Region(e.target.value)}
                      placeholder="us-east-1"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="s3AccessKey">{t('settings.accessKeyId')}</Label>
                    <Input
                      id="s3AccessKey"
                      placeholder="AKIAIOSFODNN7EXAMPLE"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="s3SecretKey">{t('settings.secretAccessKey')}</Label>
                    <Input
                      id="s3SecretKey"
                      type="password"
                      placeholder="••••••••"
                    />
                  </div>

                  <Button variant="outline" onClick={() => handleTestConnection('S3')}>
                    {t('settings.testS3Connection')}
                  </Button>
                </>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.backupConfiguration')}</CardTitle>
              <CardDescription>{t('settings.configureAutoBackups')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.enableBackups')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.autoBackupData')}
                  </p>
                </div>
                <Switch
                  checked={backupEnabled}
                  onCheckedChange={setBackupEnabled}
                />
              </div>

              {backupEnabled && (
                <>
                  <div className="space-y-2">
                    <Label htmlFor="backupSchedule">{t('settings.backupSchedule')}</Label>
                    <select
                      id="backupSchedule"
                      value={backupSchedule}
                      onChange={(e) => setBackupSchedule(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
                    >
                      <option value="hourly">{t('settings.hourly')}</option>
                      <option value="daily">{t('settings.daily')}</option>
                      <option value="weekly">{t('settings.weekly')}</option>
                      <option value="monthly">{t('settings.monthly')}</option>
                    </select>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="backupLocation">{t('settings.backupLocation')}</Label>
                    <Input
                      id="backupLocation"
                      defaultValue="/var/backups/angusrepo"
                      placeholder="/path/to/backups"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="retentionPeriod">{t('settings.retentionPeriod')}</Label>
                    <Input
                      id="retentionPeriod"
                      type="number"
                      defaultValue="30"
                      placeholder="30"
                    />
                  </div>
                </>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.storageStatistics')}</CardTitle>
              <CardDescription>{t('settings.currentStorageUsage')}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm text-gray-600 dark:text-gray-400">{t('settings.totalStorage')}</span>
                    <span className="text-sm font-medium text-gray-900 dark:text-white">287 GB / 500 GB</span>
                  </div>
                  <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                    <div className="bg-blue-600 h-2 rounded-full" style={{ width: '57%' }} />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 pt-4">
                  <div className="p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                    <p className="text-sm text-gray-600 dark:text-gray-400">{t('settings.artifacts')}</p>
                    <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">234 GB</p>
                  </div>
                  <div className="p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                    <p className="text-sm text-gray-600 dark:text-gray-400">{t('settings.backups')}</p>
                    <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">53 GB</p>
                  </div>
                </div>
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

        {/* Authentication Tab */}
        <TabsContent value="authentication" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.ldapConfiguration')}</CardTitle>
              <CardDescription>{t('settings.configureLDAP')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.enableLDAP')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.useLDAPAuth')}
                  </p>
                </div>
                <Switch
                  checked={ldapEnabled}
                  onCheckedChange={setLdapEnabled}
                />
              </div>

              {ldapEnabled && (
                <>
                  <div className="space-y-2">
                    <Label htmlFor="ldapServer">{t('settings.ldapServer')}</Label>
                    <Input
                      id="ldapServer"
                      value={ldapServer}
                      onChange={(e) => setLdapServer(e.target.value)}
                      placeholder="ldap://ldap.company.com"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="ldapBaseDn">{t('settings.baseDN')}</Label>
                    <Input
                      id="ldapBaseDn"
                      defaultValue="dc=company,dc=com"
                      placeholder="dc=company,dc=com"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="ldapBindDn">{t('settings.bindDN')}</Label>
                    <Input
                      id="ldapBindDn"
                      defaultValue="cn=admin,dc=company,dc=com"
                      placeholder="cn=admin,dc=company,dc=com"
                    />
                  </div>

                  <Button variant="outline" onClick={() => handleTestConnection('LDAP')}>
                    {t('settings.testLDAPConnection')}
                  </Button>
                </>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.samlConfiguration')}</CardTitle>
              <CardDescription>{t('settings.configureSAML')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.enableSAML')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.useSAMLSSO')}
                  </p>
                </div>
                <Switch
                  checked={samlEnabled}
                  onCheckedChange={setSamlEnabled}
                />
              </div>

              {samlEnabled && (
                <>
                  <div className="space-y-2">
                    <Label htmlFor="samlEntityId">{t('settings.entityId')}</Label>
                    <Input
                      id="samlEntityId"
                      value={samlEntityId}
                      onChange={(e) => setSamlEntityId(e.target.value)}
                      placeholder="https://repo.company.com/saml"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="samlIdpUrl">{t('settings.idpSSOUrl')}</Label>
                    <Input
                      id="samlIdpUrl"
                      defaultValue="https://idp.company.com/sso"
                      placeholder="https://idp.company.com/sso"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="samlCertificate">{t('settings.idpCertificate')}</Label>
                    <textarea
                      id="samlCertificate"
                      rows={4}
                      className="w-full px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white font-mono text-sm"
                      placeholder="-----BEGIN CERTIFICATE-----"
                    />
                  </div>
                </>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.passwordPolicy')}</CardTitle>
              <CardDescription>{t('settings.configurePasswordRequirements')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="minLength">{t('settings.minimumLength')}</Label>
                <Input
                  id="minLength"
                  type="number"
                  value={passwordPolicy.minLength}
                  onChange={(e) => setPasswordPolicy({...passwordPolicy, minLength: parseInt(e.target.value)})}
                  min="6"
                  max="32"
                />
              </div>

              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.requireUppercase')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.passwordUppercaseReq')}
                  </p>
                </div>
                <Switch
                  checked={passwordPolicy.requireUppercase}
                  onCheckedChange={(checked) => setPasswordPolicy({...passwordPolicy, requireUppercase: checked})}
                />
              </div>

              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.requireNumbers')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.passwordNumberReq')}
                  </p>
                </div>
                <Switch
                  checked={passwordPolicy.requireNumbers}
                  onCheckedChange={(checked) => setPasswordPolicy({...passwordPolicy, requireNumbers: checked})}
                />
              </div>

              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.requireSymbols')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.passwordSymbolReq')}
                  </p>
                </div>
                <Switch
                  checked={passwordPolicy.requireSymbols}
                  onCheckedChange={(checked) => setPasswordPolicy({...passwordPolicy, requireSymbols: checked})}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="expiryDays">{t('settings.passwordExpiry')}</Label>
                <Input
                  id="expiryDays"
                  type="number"
                  value={passwordPolicy.expiryDays}
                  onChange={(e) => setPasswordPolicy({...passwordPolicy, expiryDays: parseInt(e.target.value)})}
                  placeholder="90"
                />
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  {t('settings.disableExpiration')}
                </p>
              </div>
            </CardContent>
          </Card>

          <div className="flex justify-end">
            <Button onClick={handleSaveAuth}>
              <Save className="mr-2 size-4" />
              {t('settings.saveChanges')}
            </Button>
          </div>
        </TabsContent>

        {/* Integration Tab */}
        <TabsContent value="integration" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.cicdIntegrations')}</CardTitle>
              <CardDescription>{t('settings.connectCICD')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                <div className="flex items-center gap-4">
                  <div className="size-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                    <Server className="size-6 text-blue-600 dark:text-blue-400" />
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white">Jenkins</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {t('settings.integrateJenkins')}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {jenkinsEnabled && (
                    <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                      {t('settings.connected')}
                    </Badge>
                  )}
                  <Switch
                    checked={jenkinsEnabled}
                    onCheckedChange={setJenkinsEnabled}
                  />
                </div>
              </div>

              <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                <div className="flex items-center gap-4">
                  <div className="size-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
                    <Globe className="size-6 text-orange-600 dark:text-orange-400" />
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white">GitLab CI</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {t('settings.integrateGitLab')}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {gitlabEnabled && (
                    <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                      {t('settings.connected')}
                    </Badge>
                  )}
                  <Switch
                    checked={gitlabEnabled}
                    onCheckedChange={setGitlabEnabled}
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.projectManagement')}</CardTitle>
              <CardDescription>{t('settings.connectProjectTools')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                <div className="flex items-center gap-4">
                  <div className="size-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                    <Plug className="size-6 text-blue-600 dark:text-blue-400" />
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white">Jira</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {t('settings.linkJiraIssues')}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <Switch
                    checked={jiraEnabled}
                    onCheckedChange={setJiraEnabled}
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.communication')}</CardTitle>
              <CardDescription>{t('settings.connectCommPlatforms')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                <div className="flex items-center gap-4">
                  <div className="size-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                    <Mail className="size-6 text-purple-600 dark:text-purple-400" />
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white">Slack</h4>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {t('settings.sendSlackNotifications')}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  {slackEnabled && (
                    <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                      {t('settings.connected')}
                    </Badge>
                  )}
                  <Switch
                    checked={slackEnabled}
                    onCheckedChange={setSlackEnabled}
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          <div className="flex justify-end">
            <Button onClick={handleSaveIntegration}>
              <Save className="mr-2 size-4" />
              {t('settings.saveChanges')}
            </Button>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}