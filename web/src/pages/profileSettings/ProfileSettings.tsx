import { useState } from 'react';
import { User, Mail, Shield, Bell, Key, Camera, Save, Copy, Check, Plus, Trash2, Eye, EyeOff } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';

export function ProfileSettings() {
  const { t } = useLanguage();
  const [name, setName] = useState('John Anderson');
  const [email, setEmail] = useState('john.anderson@company.com');
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  // Notification settings
  const [emailNotifications, setEmailNotifications] = useState(true);
  const [pushNotifications, setPushNotifications] = useState(true);
  const [securityAlerts, setSecurityAlerts] = useState(true);
  const [downloadNotifications, setDownloadNotifications] = useState(false);
  const [weeklyReport, setWeeklyReport] = useState(true);

  // API Tokens
  const [tokens, setTokens] = useState([
    {
      id: '1',
      name: 'CI/CD Pipeline',
      token: 'angus_******************4a2f',
      created: '2024-01-15',
      lastUsed: '2 hours ago',
      permissions: 'Read/Write',
      status: 'active',
    },
    {
      id: '2',
      name: 'Development Token',
      token: 'angus_******************8b3c',
      created: '2024-01-10',
      lastUsed: 'Never',
      permissions: 'Read Only',
      status: 'inactive',
    },
    {
      id: '3',
      name: 'Production Deploy',
      token: 'angus_******************9d1e',
      created: '2023-12-20',
      lastUsed: '1 day ago',
      permissions: 'Admin',
      status: 'active',
    },
  ]);

  const [showCreateToken, setShowCreateToken] = useState(false);
  const [newTokenName, setNewTokenName] = useState('');
  const [newTokenPermission, setNewTokenPermission] = useState('read');
  const [generatedToken, setGeneratedToken] = useState('');
  const [tokenCopied, setTokenCopied] = useState(false);

  const handleSaveProfile = () => {
    toast.success(t('settings.settingsSaved'));
  };

  const handleChangePassword = () => {
    if (newPassword !== confirmPassword) {
      toast.error(t('settings.passwordsNotMatch'));
      return;
    }
    if (newPassword.length < 8) {
      toast.error(t('settings.passwordTooShort'));
      return;
    }
    toast.success(t('settings.passwordChanged'));
    setCurrentPassword('');
    setNewPassword('');
    setConfirmPassword('');
  };

  const handleCreateToken = () => {
    if (!newTokenName.trim()) {
      toast.error(t('settings.pleaseEnterTokenName'));
      return;
    }
    const token = `angus_${Math.random().toString(36).substring(2, 15)}${Math.random().toString(36).substring(2, 15)}`;
    setGeneratedToken(token);
    setNewTokenName('');
  };

  const handleCopyToken = (token: string) => {
    navigator.clipboard.writeText(token);
    setTokenCopied(true);
    toast.success(t('access.tokenCopied'));
    setTimeout(() => setTokenCopied(false), 2000);
  };

  const handleRevokeToken = (id: string, name: string) => {
    setTokens(tokens.filter(t => t.id !== id));
    toast.success(`${t('settings.webhookDeleted').replace('Webhook', t('settings.createToken'))} "${name}"`);
  };

  const handleSaveNotifications = () => {
    toast.success(t('settings.settingsSaved'));
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 dark:text-white">{t('settings.profile')}</h1>
        <p className="text-gray-600 dark:text-gray-400 mt-1">{t('settings.personalInfo')}</p>
      </div>

      <Tabs defaultValue="profile" className="space-y-6">
        <TabsList>
          <TabsTrigger value="profile">
            <User className="mr-2 size-4" />
            {t('settings.personalInfo')}
          </TabsTrigger>
          <TabsTrigger value="security">
            <Shield className="mr-2 size-4" />
            {t('settings.security')}
          </TabsTrigger>
          <TabsTrigger value="api-tokens">
            <Key className="mr-2 size-4" />
            {t('settings.apiTokens')}
          </TabsTrigger>
          <TabsTrigger value="notifications">
            <Bell className="mr-2 size-4" />
            {t('settings.notifications')}
          </TabsTrigger>
        </TabsList>

        {/* Profile Tab */}
        <TabsContent value="profile" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.personalInfo')}</CardTitle>
              <CardDescription>{t('settings.updateProfileInfo')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              {/* Avatar Upload */}
              <div className="flex items-start gap-6">
                <div className="relative">
                  <div className="size-24 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center">
                    <span className="text-3xl text-white font-semibold">JA</span>
                  </div>
                  <button className="absolute bottom-0 right-0 size-8 bg-white dark:bg-gray-800 rounded-full border-2 border-gray-200 dark:border-gray-700 flex items-center justify-center hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                    <Camera className="size-4 text-gray-600 dark:text-gray-400" />
                  </button>
                </div>
                <div className="flex-1">
                  <h3 className="text-sm font-medium text-gray-900 dark:text-white mb-2">{t('settings.profilePicture')}</h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                    {t('settings.uploadNewPicture')}
                  </p>
                  <div className="flex gap-2">
                    <Button variant="outline" size="sm">{t('settings.uploadNew')}</Button>
                    <Button variant="ghost" size="sm">{t('settings.remove')}</Button>
                  </div>
                </div>
              </div>

              {/* Name */}
              <div className="space-y-2">
                <Label htmlFor="name">{t('settings.fullName')}</Label>
                <Input
                  id="name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder={t('settings.enterFullName')}
                />
              </div>

              {/* Email */}
              <div className="space-y-2">
                <Label htmlFor="email">{t('settings.email')}</Label>
                <Input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder={t('settings.enterEmail')}
                />
              </div>

              {/* Username */}
              <div className="space-y-2">
                <Label htmlFor="username">{t('settings.username')}</Label>
                <Input
                  id="username"
                  defaultValue="john.anderson"
                  placeholder={t('settings.enterUsername')}
                />
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  {t('settings.uniqueIdentifier')}
                </p>
              </div>

              {/* Job Title */}
              <div className="space-y-2">
                <Label htmlFor="jobTitle">{t('settings.jobTitle')}</Label>
                <Input
                  id="jobTitle"
                  defaultValue="Senior DevOps Engineer"
                  placeholder={t('settings.enterJobTitle')}
                />
              </div>

              {/* Department */}
              <div className="space-y-2">
                <Label htmlFor="department">{t('settings.department')}</Label>
                <Input
                  id="department"
                  defaultValue="Engineering"
                  placeholder={t('settings.enterDepartment')}
                />
              </div>

              {/* Save Button */}
              <div className="flex justify-end">
                <Button onClick={handleSaveProfile}>
                  <Save className="mr-2 size-4" />
                  {t('settings.saveChanges')}
                </Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Security Tab */}
        <TabsContent value="security" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.changePassword')}</CardTitle>
              <CardDescription>{t('settings.updatePasswordSecurity')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Current Password */}
              <div className="space-y-2">
                <Label htmlFor="currentPassword">{t('settings.currentPassword')}</Label>
                <div className="relative">
                  <Input
                    id="currentPassword"
                    type={showCurrentPassword ? 'text' : 'password'}
                    value={currentPassword}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                    placeholder={t('settings.enterCurrentPassword')}
                  />
                  <button
                    type="button"
                    onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                  >
                    {showCurrentPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                  </button>
                </div>
              </div>

              {/* New Password */}
              <div className="space-y-2">
                <Label htmlFor="newPassword">{t('settings.newPassword')}</Label>
                <div className="relative">
                  <Input
                    id="newPassword"
                    type={showNewPassword ? 'text' : 'password'}
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    placeholder={t('settings.enterNewPassword')}
                  />
                  <button
                    type="button"
                    onClick={() => setShowNewPassword(!showNewPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                  >
                    {showNewPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                  </button>
                </div>
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  {t('settings.passwordRequirements')}
                </p>
              </div>

              {/* Confirm Password */}
              <div className="space-y-2">
                <Label htmlFor="confirmPassword">{t('settings.confirmNewPassword')}</Label>
                <div className="relative">
                  <Input
                    id="confirmPassword"
                    type={showConfirmPassword ? 'text' : 'password'}
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    placeholder={t('settings.confirmPassword')}
                  />
                  <button
                    type="button"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                  >
                    {showConfirmPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                  </button>
                </div>
              </div>

              {/* Change Password Button */}
              <div className="flex justify-end pt-4">
                <Button onClick={handleChangePassword}>
                  <Shield className="mr-2 size-4" />
                  {t('settings.changePassword')}
                </Button>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.twoFactorAuth')}</CardTitle>
              <CardDescription>{t('settings.extraSecurityLayer')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                <div>
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.authenticatorApp')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.useAuthenticatorApp')}
                  </p>
                </div>
                <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                  {t('settings.enabled')}
                </Badge>
              </div>

              <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                <div>
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.smsAuthentication')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.receiveSMSCodes')}
                  </p>
                </div>
                <Button variant="outline" size="sm">{t('settings.enable')}</Button>
              </div>

              <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                <div>
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.backupCodes')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.generateBackupCodes')}
                  </p>
                </div>
                <Button variant="outline" size="sm">{t('settings.generate')}</Button>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('settings.activeSessions')}</CardTitle>
              <CardDescription>{t('settings.manageActiveSessions')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              {[
                { device: 'Chrome on MacBook Pro', location: 'San Francisco, CA', time: t('settings.activeNow'), current: true },
                { device: 'Firefox on Windows', location: 'New York, NY', time: '2 hours ago', current: false },
                { device: 'Safari on iPhone', location: 'Los Angeles, CA', time: '1 day ago', current: false },
              ].map((session, index) => (
                <div key={index} className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <h4 className="font-medium text-gray-900 dark:text-white">{session.device}</h4>
                      {session.current && (
                        <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300">
                          {t('settings.current')}
                        </Badge>
                      )}
                    </div>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                      {session.location} · {session.time}
                    </p>
                  </div>
                  {!session.current && (
                    <Button variant="ghost" size="sm">{t('settings.revoke')}</Button>
                  )}
                </div>
              ))}
            </CardContent>
          </Card>
        </TabsContent>

        {/* API Tokens Tab */}
        <TabsContent value="api-tokens" className="space-y-6">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>{t('settings.apiTokens')}</CardTitle>
                  <CardDescription>{t('settings.manageApiTokens')}</CardDescription>
                </div>
                <Button onClick={() => setShowCreateToken(true)}>
                  <Plus className="mr-2 size-4" />
                  {t('settings.createToken')}
                </Button>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {showCreateToken && (
                <div className="p-4 border-2 border-dashed border-blue-300 dark:border-blue-700 rounded-lg bg-blue-50 dark:bg-blue-900/10 space-y-4">
                  <h3 className="font-medium text-gray-900 dark:text-white">{t('settings.createNewToken')}</h3>
                  <div className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="tokenName">{t('settings.tokenName')}</Label>
                      <Input
                        id="tokenName"
                        value={newTokenName}
                        onChange={(e) => setNewTokenName(e.target.value)}
                        placeholder={t('settings.tokenNamePlaceholder')}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="permission">{t('settings.permission')}</Label>
                      <select
                        id="permission"
                        value={newTokenPermission}
                        onChange={(e) => setNewTokenPermission(e.target.value)}
                        className="w-full px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
                      >
                        <option value="read">{t('settings.readOnly')}</option>
                        <option value="write">{t('settings.readWrite')}</option>
                        <option value="admin">{t('settings.admin')}</option>
                      </select>
                    </div>
                    <div className="flex gap-2">
                      <Button onClick={handleCreateToken}>{t('settings.generateToken')}</Button>
                      <Button variant="ghost" onClick={() => {
                        setShowCreateToken(false);
                        setNewTokenName('');
                        setGeneratedToken('');
                      }}>
                        {t('settings.cancel')}
                      </Button>
                    </div>
                  </div>

                  {generatedToken && (
                    <div className="mt-4 p-4 bg-yellow-50 dark:bg-yellow-900/20 border border-yellow-200 dark:border-yellow-800 rounded-lg">
                      <h4 className="font-medium text-gray-900 dark:text-white mb-2">
                        {t('settings.saveTokenWarning')}
                      </h4>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                        {t('settings.tokenOnceWarning')}
                      </p>
                      <div className="flex items-center gap-2">
                        <Input
                          value={generatedToken}
                          readOnly
                          className="font-mono text-sm"
                        />
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleCopyToken(generatedToken)}
                        >
                          {tokenCopied ? <Check className="size-4" /> : <Copy className="size-4" />}
                        </Button>
                      </div>
                    </div>
                  )}
                </div>
              )}

              {tokens.map((token) => (
                <div key={token.id} className="p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                  <div className="flex items-start justify-between mb-3">
                    <div>
                      <div className="flex items-center gap-2 mb-1">
                        <h4 className="font-medium text-gray-900 dark:text-white">{token.name}</h4>
                        <Badge
                          className={
                            token.status === 'active'
                              ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300'
                              : 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300'
                          }
                        >
                          {token.status === 'active' ? t('settings.active') : t('settings.inactive')}
                        </Badge>
                      </div>
                      <p className="text-sm text-gray-600 dark:text-gray-400 font-mono">
                        {token.token}
                      </p>
                    </div>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleRevokeToken(token.id, token.name)}
                    >
                      <Trash2 className="size-4 text-red-500" />
                    </Button>
                  </div>
                  <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                    <span>{t('settings.created')}: {token.created}</span>
                    <span>•</span>
                    <span>{t('settings.lastUsed')}: {token.lastUsed}</span>
                    <span>•</span>
                    <span>{t('settings.permission')}: {token.permissions}</span>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        </TabsContent>

        {/* Notifications Tab */}
        <TabsContent value="notifications" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>{t('settings.notificationPreferences')}</CardTitle>
              <CardDescription>{t('settings.chooseNotifications')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              {/* Email Notifications */}
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.emailNotifications')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.receiveEmailNotifications')}
                  </p>
                </div>
                <Switch
                  checked={emailNotifications}
                  onCheckedChange={setEmailNotifications}
                />
              </div>

              {/* Push Notifications */}
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.pushNotifications')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.receiveBrowserNotifications')}
                  </p>
                </div>
                <Switch
                  checked={pushNotifications}
                  onCheckedChange={setPushNotifications}
                />
              </div>

              {/* Security Alerts */}
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.securityAlerts')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.securityVulnerabilities')}
                  </p>
                </div>
                <Switch
                  checked={securityAlerts}
                  onCheckedChange={setSecurityAlerts}
                />
              </div>

              {/* Download Notifications */}
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.downloadNotifications')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.notifyDownloads')}
                  </p>
                </div>
                <Switch
                  checked={downloadNotifications}
                  onCheckedChange={setDownloadNotifications}
                />
              </div>

              {/* Weekly Report */}
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-white">{t('settings.weeklyActivityReport')}</h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                    {t('settings.weeklyReportSummary')}
                  </p>
                </div>
                <Switch
                  checked={weeklyReport}
                  onCheckedChange={setWeeklyReport}
                />
              </div>

              {/* Save Button */}
              <div className="flex justify-end pt-4 border-t border-gray-200 dark:border-gray-700">
                <Button onClick={handleSaveNotifications}>
                  <Save className="mr-2 size-4" />
                  {t('settings.savePreferences')}
                </Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}