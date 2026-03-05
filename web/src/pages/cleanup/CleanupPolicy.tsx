import { useState } from 'react';
import { 
  Trash2, Plus, Edit, Play, Clock, Calendar, Package,
  AlertTriangle, CheckCircle, Settings, FileText, X,
  Filter, HardDrive, TrendingDown, Archive, Search
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Switch } from '@/components/ui/switch';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Separator } from '@/components/ui/separator';
import { ScrollArea } from '@/components/ui/scroll-area';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';

interface CleanupPolicy {
  id: string;
  name: string;
  repository: string;
  format: string;
  enabled: boolean;
  criteria: {
    type: 'lastDownloaded' | 'lastModified' | 'publishedBefore' | 'componentAge';
    value: number;
    unit: 'days' | 'weeks' | 'months' | 'years';
  };
  releaseType: 'releases' | 'prereleases' | 'snapshots' | 'all';
  schedule: 'manual' | 'daily' | 'weekly' | 'monthly';
  lastRun?: string;
  itemsDeleted?: number;
  spaceReclaimed?: string;
}

const mockPolicies: CleanupPolicy[] = [
  {
    id: '1',
    name: 'Clean Old Snapshots',
    repository: 'maven-snapshots',
    format: 'Maven',
    enabled: true,
    criteria: {
      type: 'lastModified',
      value: 30,
      unit: 'days'
    },
    releaseType: 'snapshots',
    schedule: 'weekly',
    lastRun: '2024-01-15T10:00:00Z',
    itemsDeleted: 45,
    spaceReclaimed: '2.3 GB'
  },
  {
    id: '2',
    name: 'Remove Unused Docker Images',
    repository: 'docker-registry',
    format: 'Docker',
    enabled: true,
    criteria: {
      type: 'lastDownloaded',
      value: 90,
      unit: 'days'
    },
    releaseType: 'all',
    schedule: 'monthly',
    lastRun: '2024-01-01T00:00:00Z',
    itemsDeleted: 12,
    spaceReclaimed: '45.6 GB'
  },
  {
    id: '3',
    name: 'Clean Old NPM Packages',
    repository: 'npm-public',
    format: 'NPM',
    enabled: false,
    criteria: {
      type: 'componentAge',
      value: 6,
      unit: 'months'
    },
    releaseType: 'prereleases',
    schedule: 'weekly'
  }
];

export function CleanupPolicy() {
  const { t } = useLanguage();
  const [policies, setPolicies] = useState(mockPolicies);
  const [showCreateDialog, setShowCreateDialog] = useState(false);
  const [showPreview, setShowPreview] = useState(false);
  const [selectedPolicy, setSelectedPolicy] = useState<CleanupPolicy | null>(null);
  
  // Search and pagination
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  // Form state
  const [policyName, setPolicyName] = useState('');
  const [repository, setRepository] = useState('maven-releases');
  const [format, setFormat] = useState('Maven');
  const [criteriaType, setCriteriaType] = useState<'lastDownloaded' | 'lastModified' | 'publishedBefore' | 'componentAge'>('lastModified');
  const [criteriaValue, setCriteriaValue] = useState('30');
  const [criteriaUnit, setCriteriaUnit] = useState<'days' | 'weeks' | 'months' | 'years'>('days');
  const [releaseType, setReleaseType] = useState<'releases' | 'prereleases' | 'snapshots' | 'all'>('all');
  const [schedule, setSchedule] = useState<'manual' | 'daily' | 'weekly' | 'monthly'>('weekly');
  const [enabled, setEnabled] = useState(true);

  const handleCreatePolicy = () => {
    if (!policyName) {
      toast.error(t('cleanup.policyNameRequired'));
      return;
    }

    const newPolicy: CleanupPolicy = {
      id: Date.now().toString(),
      name: policyName,
      repository,
      format,
      enabled,
      criteria: {
        type: criteriaType,
        value: parseInt(criteriaValue),
        unit: criteriaUnit
      },
      releaseType,
      schedule
    };

    setPolicies([...policies, newPolicy]);
    setShowCreateDialog(false);
    resetForm();
    toast.success(t('cleanup.policyCreated'));
  };

  const resetForm = () => {
    setPolicyName('');
    setRepository('maven-releases');
    setFormat('Maven');
    setCriteriaType('lastModified');
    setCriteriaValue('30');
    setCriteriaUnit('days');
    setReleaseType('all');
    setSchedule('weekly');
    setEnabled(true);
  };

  const handleDeletePolicy = (id: string) => {
    setPolicies(policies.filter(p => p.id !== id));
    toast.success(t('cleanup.policyDeleted'));
  };

  const handleTogglePolicy = (id: string) => {
    setPolicies(policies.map(p => 
      p.id === id ? { ...p, enabled: !p.enabled } : p
    ));
    const policy = policies.find(p => p.id === id);
    if (policy) {
      toast.success(policy.enabled ? t('cleanup.policyDisabled') : t('cleanup.policyEnabled'));
    }
  };

  const handleExecutePolicy = (policy: CleanupPolicy) => {
    setSelectedPolicy(policy);
    setShowPreview(true);
  };

  const handleConfirmExecution = () => {
    if (selectedPolicy) {
      toast.success(t('cleanup.policyExecuted'));
      setShowPreview(false);
      setSelectedPolicy(null);
      
      // Update policy with execution results
      setPolicies(policies.map(p => 
        p.id === selectedPolicy.id 
          ? {
              ...p,
              lastRun: new Date().toISOString(),
              itemsDeleted: Math.floor(Math.random() * 50) + 10,
              spaceReclaimed: `${(Math.random() * 10 + 1).toFixed(1)} GB`
            }
          : p
      ));
    }
  };

  const totalPolicies = policies.length;
  const enabledPolicies = policies.filter(p => p.enabled).length;
  const totalItemsDeleted = policies.reduce((sum, p) => sum + (p.itemsDeleted || 0), 0);
  const totalSpaceReclaimed = policies.reduce((sum, p) => {
    const space = p.spaceReclaimed ? parseFloat(p.spaceReclaimed) : 0;
    return sum + space;
  }, 0);

  const filteredPolicies = policies.filter(p => 
    p.name.toLowerCase().includes(searchQuery.toLowerCase())
  );
  const totalPages = Math.ceil(filteredPolicies.length / itemsPerPage);
  const currentPolicies = filteredPolicies.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  return (
    <div className="flex flex-col h-[calc(100vh-8rem)]">
      {/* Header */}
      <div className="mb-6">
        <div className="mb-4">
          <h1 className="text-3xl text-gray-900 dark:text-white mb-2">
            {t('cleanup.title')}
          </h1>
          <p className="text-gray-600 dark:text-gray-400">
            {t('cleanup.description')}
          </p>
        </div>
      </div>

      {/* Statistics */}
      <div className="grid grid-cols-4 gap-4 mb-6">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-gray-600 dark:text-gray-400">{t('cleanup.totalPolicies')}</p>
              <Settings className="size-8 text-blue-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-gray-900 dark:text-white">{totalPolicies}</p>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
              {enabledPolicies} {t('cleanup.active')}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-gray-600 dark:text-gray-400">{t('cleanup.itemsDeleted')}</p>
              <Trash2 className="size-8 text-red-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-gray-900 dark:text-white">{totalItemsDeleted}</p>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
              {t('cleanup.thisMonth')}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-gray-600 dark:text-gray-400">{t('cleanup.spaceReclaimed')}</p>
              <HardDrive className="size-8 text-green-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-gray-900 dark:text-white">
              {totalSpaceReclaimed.toFixed(1)} GB
            </p>
            <p className="text-sm text-green-600 dark:text-green-400 mt-1">
              <TrendingDown className="inline size-3" /> {t('cleanup.storageSaved')}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-gray-600 dark:text-gray-400">{t('cleanup.nextScheduled')}</p>
              <Clock className="size-8 text-purple-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-gray-900 dark:text-white">2</p>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
              {t('cleanup.in24Hours')}
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Policy List */}
      <Card className="flex-1 overflow-hidden flex flex-col">
        <CardContent className="space-y-4 pt-6 flex flex-col flex-1 overflow-hidden">
          {/* Search and Create Button Row */}
          <div className="flex items-center gap-3">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
              <Input
                placeholder={t('cleanup.searchPolicies') || 'Search cleanup policies...'}
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                  setCurrentPage(1); // Reset to first page on search
                }}
                className="pl-10"
              />
            </div>
            <Button onClick={() => setShowCreateDialog(true)}>
              <Plus className="mr-2 size-4" />
              {t('cleanup.createPolicy')}
            </Button>
          </div>

          {/* Policy List with ScrollArea */}
          <ScrollArea className="flex-1 pr-3">
            <div className="space-y-3">
              {currentPolicies.map((policy) => (
                <div 
                  key={policy.id}
                  className="p-4 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors"
                >
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <h3 className="font-semibold text-gray-900 dark:text-white">
                          {policy.name}
                        </h3>
                        <Badge variant={policy.enabled ? 'default' : 'secondary'}>
                          {policy.enabled ? t('cleanup.enabled') : t('cleanup.disabled')}
                        </Badge>
                        <Badge variant="outline">{policy.format}</Badge>
                      </div>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                        {policy.repository} • {t(`cleanup.${policy.criteria.type}`)} {policy.criteria.value} {t(`cleanup.${policy.criteria.unit}`)} • {t(`cleanup.${policy.schedule}`)}
                      </p>
                      {policy.lastRun && (
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                          <div className="flex items-center gap-1">
                            <Clock className="size-4" />
                            <span>{t('cleanup.lastRun')}: {new Date(policy.lastRun).toLocaleString()}</span>
                          </div>
                          <div className="flex items-center gap-1">
                            <Trash2 className="size-4" />
                            <span>{policy.itemsDeleted} {t('cleanup.items')}</span>
                          </div>
                          <div className="flex items-center gap-1 text-green-600 dark:text-green-400">
                            <HardDrive className="size-4" />
                            <span>{policy.spaceReclaimed}</span>
                          </div>
                        </div>
                      )}
                    </div>
                    <div className="flex items-center gap-2">
                      <Switch
                        checked={policy.enabled}
                        onCheckedChange={() => handleTogglePolicy(policy.id)}
                      />
                      <Button 
                        variant="outline" 
                        size="sm"
                        onClick={() => handleExecutePolicy(policy)}
                        disabled={!policy.enabled}
                      >
                        <Play className="size-4" />
                      </Button>
                      <Button 
                        variant="ghost" 
                        size="sm"
                        onClick={() => handleDeletePolicy(policy.id)}
                      >
                        <Trash2 className="size-4 text-red-600 dark:text-red-400" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </ScrollArea>

          {/* Pagination */}
          {filteredPolicies.length > itemsPerPage && (
            <div className="flex items-center justify-between border-t border-gray-200 dark:border-gray-700 pt-4">
              <div className="text-sm text-gray-600 dark:text-gray-400">
                Showing {((currentPage - 1) * itemsPerPage) + 1} to {Math.min(currentPage * itemsPerPage, filteredPolicies.length)} of {filteredPolicies.length} policies
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                  disabled={currentPage === 1}
                >
                  Previous
                </Button>
                <div className="flex items-center gap-1">
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                    <Button
                      key={page}
                      variant={page === currentPage ? "default" : "outline"}
                      size="sm"
                      onClick={() => setCurrentPage(page)}
                      className="min-w-[40px]"
                    >
                      {page}
                    </Button>
                  ))}
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                  disabled={currentPage === totalPages}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Create Policy Dialog */}
      {showCreateDialog && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => setShowCreateDialog(false)}>
          <Card className="w-full max-w-2xl" onClick={(e) => e.stopPropagation()}>
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle>{t('cleanup.createPolicy')}</CardTitle>
                <Button variant="ghost" size="sm" onClick={() => setShowCreateDialog(false)}>
                  <X className="size-5" />
                </Button>
              </div>
              <CardDescription>{t('cleanup.createPolicyDesc')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="space-y-4">
                <div>
                  <Label htmlFor="policyName">{t('cleanup.policyName')} *</Label>
                  <Input
                    id="policyName"
                    value={policyName}
                    onChange={(e) => setPolicyName(e.target.value)}
                    placeholder="e.g., Clean Old Snapshots"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="repository">{t('cleanup.repository')}</Label>
                    <Select value={repository} onValueChange={setRepository}>
                      <SelectTrigger id="repository">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="maven-releases">maven-releases</SelectItem>
                        <SelectItem value="maven-snapshots">maven-snapshots</SelectItem>
                        <SelectItem value="docker-registry">docker-registry</SelectItem>
                        <SelectItem value="npm-public">npm-public</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div>
                    <Label htmlFor="format">{t('cleanup.format')}</Label>
                    <Select value={format} onValueChange={setFormat}>
                      <SelectTrigger id="format">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Maven">Maven</SelectItem>
                        <SelectItem value="Docker">Docker</SelectItem>
                        <SelectItem value="NPM">NPM</SelectItem>
                        <SelectItem value="PyPI">PyPI</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>

                <Separator />

                <div>
                  <Label>{t('cleanup.cleanupCriteria')}</Label>
                  <div className="grid grid-cols-3 gap-4 mt-2">
                    <Select value={criteriaType} onValueChange={(v) => setCriteriaType(v as any)}>
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="lastDownloaded">{t('cleanup.lastDownloaded')}</SelectItem>
                        <SelectItem value="lastModified">{t('cleanup.lastModified')}</SelectItem>
                        <SelectItem value="publishedBefore">{t('cleanup.publishedBefore')}</SelectItem>
                        <SelectItem value="componentAge">{t('cleanup.componentAge')}</SelectItem>
                      </SelectContent>
                    </Select>
                    <Input
                      type="number"
                      value={criteriaValue}
                      onChange={(e) => setCriteriaValue(e.target.value)}
                      placeholder="30"
                    />
                    <Select value={criteriaUnit} onValueChange={(v) => setCriteriaUnit(v as any)}>
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="days">{t('cleanup.days')}</SelectItem>
                        <SelectItem value="weeks">{t('cleanup.weeks')}</SelectItem>
                        <SelectItem value="months">{t('cleanup.months')}</SelectItem>
                        <SelectItem value="years">{t('cleanup.years')}</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="releaseType">{t('cleanup.releaseType')}</Label>
                    <Select value={releaseType} onValueChange={(v) => setReleaseType(v as any)}>
                      <SelectTrigger id="releaseType">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="all">{t('cleanup.allTypes')}</SelectItem>
                        <SelectItem value="releases">{t('cleanup.releases')}</SelectItem>
                        <SelectItem value="prereleases">{t('cleanup.prereleases')}</SelectItem>
                        <SelectItem value="snapshots">{t('cleanup.snapshots')}</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div>
                    <Label htmlFor="schedule">{t('cleanup.schedule')}</Label>
                    <Select value={schedule} onValueChange={(v) => setSchedule(v as any)}>
                      <SelectTrigger id="schedule">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="manual">{t('cleanup.manual')}</SelectItem>
                        <SelectItem value="daily">{t('cleanup.daily')}</SelectItem>
                        <SelectItem value="weekly">{t('cleanup.weekly')}</SelectItem>
                        <SelectItem value="monthly">{t('cleanup.monthly')}</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>

                <div className="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900 dark:text-white">{t('cleanup.enablePolicy')}</p>
                    <p className="text-sm text-gray-600 dark:text-gray-400">{t('cleanup.enablePolicyDesc')}</p>
                  </div>
                  <Switch checked={enabled} onCheckedChange={setEnabled} />
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-4 border-t">
                <Button variant="outline" onClick={() => setShowCreateDialog(false)}>
                  {t('common.cancel')}
                </Button>
                <Button onClick={handleCreatePolicy}>
                  {t('common.create')}
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Preview Dialog */}
      {showPreview && selectedPolicy && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => setShowPreview(false)}>
          <Card className="w-full max-w-2xl" onClick={(e) => e.stopPropagation()}>
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle>{t('cleanup.previewResults')}</CardTitle>
                <Button variant="ghost" size="sm" onClick={() => setShowPreview(false)}>
                  <X className="size-5" />
                </Button>
              </div>
              <CardDescription>{selectedPolicy.name}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="p-4 bg-yellow-50 dark:bg-yellow-950/20 border border-yellow-200 dark:border-yellow-900 rounded-lg">
                <div className="flex items-start gap-3">
                  <AlertTriangle className="size-6 text-yellow-600 dark:text-yellow-400 flex-shrink-0 mt-0.5" />
                  <div>
                    <p className="font-medium text-yellow-900 dark:text-yellow-100 mb-1">
                      {t('cleanup.confirmExecution')}
                    </p>
                    <p className="text-sm text-yellow-700 dark:text-yellow-300">
                      {t('cleanup.confirmExecutionDesc')}
                    </p>
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <Card>
                  <CardContent className="pt-6">
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                      {t('cleanup.itemsToDelete')}
                    </p>
                    <p className="text-3xl font-bold text-gray-900 dark:text-white">
                      {Math.floor(Math.random() * 50) + 10}
                    </p>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="pt-6">
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                      {t('cleanup.estimatedSpace')}
                    </p>
                    <p className="text-3xl font-bold text-green-600 dark:text-green-400">
                      {(Math.random() * 10 + 1).toFixed(1)} GB
                    </p>
                  </CardContent>
                </Card>
              </div>

              <div className="space-y-2">
                <p className="text-sm font-medium text-gray-900 dark:text-white">
                  {t('cleanup.artifactsToRemove')}:
                </p>
                <div className="max-h-48 overflow-y-auto space-y-2 p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                  {Array.from({ length: 5 }).map((_, idx) => (
                    <div key={idx} className="flex items-center gap-2 text-sm">
                      <Archive className="size-4 text-gray-400" />
                      <span className="text-gray-900 dark:text-white">
                        artifact-{idx + 1}-1.0.{idx}-SNAPSHOT.jar
                      </span>
                      <span className="text-gray-600 dark:text-gray-400 ml-auto">
                        {(Math.random() * 5).toFixed(1)} MB
                      </span>
                    </div>
                  ))}
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-4 border-t">
                <Button variant="outline" onClick={() => setShowPreview(false)}>
                  {t('common.cancel')}
                </Button>
                <Button variant="destructive" onClick={handleConfirmExecution}>
                  <Trash2 className="mr-2 size-4" />
                  {t('cleanup.executeCleanup')}
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}