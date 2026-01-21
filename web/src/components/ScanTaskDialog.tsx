import { useState } from 'react';
import { X, Zap, Calendar, Target, Clock } from 'lucide-react';
import { Button } from './ui/button';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Separator } from './ui/separator';
import { Switch } from './ui/switch';
import { useLanguage } from './LanguageProvider';
import { toast } from 'sonner';

interface ScanTaskDialogProps {
  onClose: () => void;
}

export function ScanTaskDialog({ onClose }: ScanTaskDialogProps) {
  const { t } = useLanguage();
  const [taskName, setTaskName] = useState('');
  const [repository, setRepository] = useState('all');
  const [scanType, setScanType] = useState('full');
  const [schedule, setSchedule] = useState('manual');
  const [enabled, setEnabled] = useState(true);

  const handleCreate = () => {
    if (!taskName) {
      toast.error(t('security.taskNameRequired'));
      return;
    }
    
    toast.success(t('security.taskCreated'));
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={onClose}>
      <Card className="w-full max-w-2xl max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <CardTitle className="text-xl flex items-center gap-2">
                <Zap className="size-5 text-blue-600" />
                {t('security.createScanTask')}
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
              <Label htmlFor="taskName">{t('security.taskName')}</Label>
              <Input
                id="taskName"
                placeholder={t('security.enterTaskName')}
                value={taskName}
                onChange={(e) => setTaskName(e.target.value)}
                className="mt-2"
              />
            </div>

            <div>
              <Label htmlFor="repository">{t('security.targetRepository')}</Label>
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

          {/* Scan Configuration */}
          <div className="space-y-4">
            <h3 className="font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <Target className="size-4" />
              {t('security.scanConfiguration')}
            </h3>

            <div>
              <Label htmlFor="scanType">{t('security.scanType')}</Label>
              <Select value={scanType} onValueChange={setScanType}>
                <SelectTrigger id="scanType" className="mt-2">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="full">{t('security.fullScan')}</SelectItem>
                  <SelectItem value="incremental">{t('security.incrementalScan')}</SelectItem>
                  <SelectItem value="critical">{t('security.criticalOnly')}</SelectItem>
                </SelectContent>
              </Select>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                {scanType === 'full' && t('security.fullScanDesc')}
                {scanType === 'incremental' && t('security.incrementalScanDesc')}
                {scanType === 'critical' && t('security.criticalOnlyDesc')}
              </p>
            </div>
          </div>

          <Separator />

          {/* Schedule */}
          <div className="space-y-4">
            <h3 className="font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <Calendar className="size-4" />
              {t('security.schedule')}
            </h3>

            <div>
              <Label htmlFor="schedule">{t('security.executionSchedule')}</Label>
              <Select value={schedule} onValueChange={setSchedule}>
                <SelectTrigger id="schedule" className="mt-2">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="manual">{t('security.manual')}</SelectItem>
                  <SelectItem value="daily">{t('security.daily')}</SelectItem>
                  <SelectItem value="weekly">{t('security.weekly')}</SelectItem>
                  <SelectItem value="monthly">{t('security.monthly')}</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div>
                <Label>{t('security.enableTask')}</Label>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  {t('security.enableTaskDesc')}
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
              <Zap className="mr-2 size-4" />
              {t('security.create')}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
