import { useState } from 'react';
import { X, Settings, Target, Shield, AlertTriangle } from 'lucide-react';
import { Button } from '@/components/ui/button';  
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Separator } from '@/components/ui/separator';
import { Switch } from '@/components/ui/switch';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';

interface ScanPolicyDialogProps {
  onClose: () => void;
}

export function ScanPolicyDialog({ onClose }: ScanPolicyDialogProps) {
  const { t } = useLanguage();
  const [policyName, setPolicyName] = useState('');
  const [repository, setRepository] = useState('all');
  const [minSeverity, setMinSeverity] = useState('medium');
  const [autoRemediate, setAutoRemediate] = useState(false);
  const [blockOnCritical, setBlockOnCritical] = useState(true);
  const [enabled, setEnabled] = useState(true);

  const handleCreate = () => {
    if (!policyName) {
      toast.error(t('security.policyNameRequired'));
      return;
    }
    
    toast.success(t('security.policyCreated'));
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={onClose}>
      <Card className="w-full max-w-2xl max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <CardTitle className="text-xl flex items-center gap-2">
                <Settings className="size-5 text-blue-600" />
                {t('security.createScanPolicy')}
              </CardTitle>
            </div>
            <Button variant="ghost" size="sm" onClick={onClose}>
              <X className="size-5" />
            </Button>
          </div>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Basic Information */}
          <div className="space-y-4">
            <div>
              <Label htmlFor="policyName">{t('security.policyName')}</Label>
              <Input
                id="policyName"
                placeholder={t('security.enterPolicyName')}
                value={policyName}
                onChange={(e) => setPolicyName(e.target.value)}
                className="mt-2"
              />
            </div>

            <div>
              <Label htmlFor="repository">{t('security.applyToRepository')}</Label>
              <Select value={repository} onValueChange={setRepository}>
                <SelectTrigger id="repository" className="mt-2">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">{t('security.allRepositories')}</SelectItem>
                  <SelectItem value="maven-releases">maven-releases</SelectItem>
                  <SelectItem value="docker-registry">docker-registry</SelectItem>
                  <SelectItem value="npm-public">npm-public</SelectItem>
                  <SelectItem value="pypi-public">pypi-public</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <Separator />

          {/* Security Rules */}
          <div className="space-y-4">
            <h3 className="font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <Shield className="size-4" />
              {t('security.securityRules')}
            </h3>

            <div>
              <Label htmlFor="minSeverity">{t('security.minimumSeverity')}</Label>
              <Select value={minSeverity} onValueChange={setMinSeverity}>
                <SelectTrigger id="minSeverity" className="mt-2">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="critical">{t('security.critical')}</SelectItem>
                  <SelectItem value="high">{t('security.high')}</SelectItem>
                  <SelectItem value="medium">{t('security.medium')}</SelectItem>
                  <SelectItem value="low">{t('security.low')}</SelectItem>
                </SelectContent>
              </Select>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                {t('security.minimumSeverityDesc')}
              </p>
            </div>

            <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div>
                <Label>{t('security.blockOnCritical')}</Label>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  {t('security.blockOnCriticalDesc')}
                </p>
              </div>
              <Switch checked={blockOnCritical} onCheckedChange={setBlockOnCritical} />
            </div>

            <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div>
                <Label>{t('security.autoRemediate')}</Label>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  {t('security.autoRemediateDesc')}
                </p>
              </div>
              <Switch checked={autoRemediate} onCheckedChange={setAutoRemediate} />
            </div>
          </div>

          <Separator />

          {/* Policy Status */}
          <div className="space-y-4">
            <h3 className="font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <AlertTriangle className="size-4" />
              {t('security.policyStatus')}
            </h3>

            <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div>
                <Label>{t('security.enablePolicy')}</Label>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  {t('security.enablePolicyDesc')}
                </p>
              </div>
              <Switch checked={enabled} onCheckedChange={setEnabled} />
            </div>
          </div>

          <Separator />

          {/* Actions */}
          <div className="flex items-center justify-end gap-2">
            <Button variant="outline" onClick={onClose}>
              {t('security.cancel')}
            </Button>
            <Button onClick={handleCreate}>
              <Settings className="mr-2 size-4" />
              {t('security.create')}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
