import { useState } from 'react';
import { Shield, AlertTriangle, AlertCircle, Info, Search, Play, Clock, FileText, ExternalLink } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Progress } from '@/components/ui/progress';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { toast } from 'sonner';

export function Security() {
  const { t } = useLanguage();
  const [searchQuery, setSearchQuery] = useState('');
  const [filterSeverity, setFilterSeverity] = useState('all');
  const [scanning, setScanning] = useState(false);
  const [scanProgress, setScanProgress] = useState(0);

  const vulnerabilities = [
    {
      id: 'CVE-2023-12345',
      artifact: 'spring-boot-starter-web:3.1.0',
      severity: 'critical',
      score: 9.8,
      description: 'Remote code execution vulnerability in Spring Boot',
      affectedVersions: '< 3.1.5',
      fixedVersions: '>= 3.1.5',
      publishedDate: '2023-12-01',
      repository: 'maven-releases',
    },
    {
      id: 'CVE-2023-12346',
      artifact: 'jackson-databind:2.14.0',
      severity: 'high',
      score: 7.5,
      description: 'Deserialization vulnerability allowing arbitrary code execution',
      affectedVersions: '< 2.14.3',
      fixedVersions: '>= 2.14.3',
      publishedDate: '2023-11-15',
      repository: 'maven-releases',
    },
    {
      id: 'CVE-2023-12347',
      artifact: 'lodash:4.17.20',
      severity: 'medium',
      score: 5.3,
      description: 'Prototype pollution vulnerability',
      affectedVersions: '< 4.17.21',
      fixedVersions: '>= 4.17.21',
      publishedDate: '2023-10-20',
      repository: 'npm-private',
    },
    {
      id: 'CVE-2023-12348',
      artifact: 'django:3.2.0',
      severity: 'high',
      score: 7.0,
      description: 'SQL injection vulnerability in ORM',
      affectedVersions: '< 3.2.23',
      fixedVersions: '>= 3.2.23',
      publishedDate: '2023-12-10',
      repository: 'pypi-public',
    },
    {
      id: 'CVE-2023-12349',
      artifact: 'axios:0.21.0',
      severity: 'low',
      score: 3.7,
      description: 'Information disclosure through error messages',
      affectedVersions: '< 0.21.4',
      fixedVersions: '>= 0.21.4',
      publishedDate: '2023-09-05',
      repository: 'npm-private',
    },
  ];

  const licenseIssues = [
    {
      id: '1',
      artifact: 'proprietary-lib:1.0.0',
      license: 'Commercial',
      type: 'incompatible',
      description: 'Commercial license not compatible with MIT',
      repository: 'maven-releases',
    },
    {
      id: '2',
      artifact: 'gpl-component:2.1.0',
      license: 'GPL-3.0',
      type: 'incompatible',
      description: 'GPL license may conflict with commercial use',
      repository: 'npm-private',
    },
    {
      id: '3',
      artifact: 'unknown-package:1.5.0',
      license: 'Unknown',
      type: 'unknown',
      description: 'License information not found',
      repository: 'pypi-public',
    },
  ];

  const scanHistory = [
    {
      id: '1',
      repository: 'maven-releases',
      scanDate: '2024-01-18 14:30:00',
      duration: '5m 23s',
      vulnerabilities: { critical: 1, high: 2, medium: 3, low: 5 },
      status: 'completed',
    },
    {
      id: '2',
      repository: 'docker-registry',
      scanDate: '2024-01-18 12:15:00',
      duration: '8m 45s',
      vulnerabilities: { critical: 0, high: 1, medium: 2, low: 3 },
      status: 'completed',
    },
    {
      id: '3',
      repository: 'npm-private',
      scanDate: '2024-01-17 16:20:00',
      duration: '3m 12s',
      vulnerabilities: { critical: 0, high: 0, medium: 1, low: 2 },
      status: 'completed',
    },
  ];

  const severityBadgeColor = (severity: string) => {
    const colors: Record<string, string> = {
      'critical': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
      'high': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
      'medium': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300',
      'low': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
    };
    return colors[severity] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  const severityIcon = (severity: string) => {
    switch (severity) {
      case 'critical':
      case 'high':
        return <AlertTriangle className="size-4" />;
      case 'medium':
        return <AlertCircle className="size-4" />;
      case 'low':
        return <Info className="size-4" />;
      default:
        return <Shield className="size-4" />;
    }
  };

  const filteredVulnerabilities = vulnerabilities.filter(vuln => {
    const matchesSearch = vuln.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         vuln.artifact.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesSeverity = filterSeverity === 'all' || vuln.severity === filterSeverity;
    return matchesSearch && matchesSeverity;
  });

  const handleScan = () => {
    setScanning(true);
    setScanProgress(0);
    
    const interval = setInterval(() => {
      setScanProgress(prev => {
        const next = prev + Math.random() * 15;
        if (next >= 100) {
          clearInterval(interval);
          setScanning(false);
          toast.success(t('security.scanStarted'));
          return 100;
        }
        return next;
      });
    }, 300);
  };

  const stats = {
    critical: vulnerabilities.filter(v => v.severity === 'critical').length,
    high: vulnerabilities.filter(v => v.severity === 'high').length,
    medium: vulnerabilities.filter(v => v.severity === 'medium').length,
    low: vulnerabilities.filter(v => v.severity === 'low').length,
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl text-gray-900 dark:text-white mb-2">{t('security.title')}</h1>
          <p className="text-gray-600 dark:text-gray-400">{t('security.description')}</p>
        </div>
        <Button onClick={handleScan} disabled={scanning}>
          <Play className="mr-2 size-4" />
          {scanning ? t('security.scanning') : t('security.scanNow')}
        </Button>
      </div>

      {/* Scanning Progress */}
      {scanning && (
        <Alert>
          <AlertCircle className="size-4" />
          <AlertTitle>Scanning in progress</AlertTitle>
          <AlertDescription className="mt-2">
            <Progress value={scanProgress} className="h-2" />
            <p className="text-sm mt-2">{Math.round(scanProgress)}% complete</p>
          </AlertDescription>
        </Alert>
      )}

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between mb-2">
              <div className="p-2 rounded-lg bg-red-100 dark:bg-red-900/30">
                <AlertTriangle className="size-6 text-red-600 dark:text-red-400" />
              </div>
              <span className="text-2xl text-gray-900 dark:text-white">{stats.critical}</span>
            </div>
            <p className="text-sm text-gray-600 dark:text-gray-400">{t('security.critical')}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between mb-2">
              <div className="p-2 rounded-lg bg-orange-100 dark:bg-orange-900/30">
                <AlertCircle className="size-6 text-orange-600 dark:text-orange-400" />
              </div>
              <span className="text-2xl text-gray-900 dark:text-white">{stats.high}</span>
            </div>
            <p className="text-sm text-gray-600 dark:text-gray-400">{t('security.high')}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between mb-2">
              <div className="p-2 rounded-lg bg-yellow-100 dark:bg-yellow-900/30">
                <AlertCircle className="size-6 text-yellow-600 dark:text-yellow-400" />
              </div>
              <span className="text-2xl text-gray-900 dark:text-white">{stats.medium}</span>
            </div>
            <p className="text-sm text-gray-600 dark:text-gray-400">{t('security.medium')}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between mb-2">
              <div className="p-2 rounded-lg bg-blue-100 dark:bg-blue-900/30">
                <Info className="size-6 text-blue-600 dark:text-blue-400" />
              </div>
              <span className="text-2xl text-gray-900 dark:text-white">{stats.low}</span>
            </div>
            <p className="text-sm text-gray-600 dark:text-gray-400">{t('security.low')}</p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="vulnerabilities">
        <TabsList>
          <TabsTrigger value="vulnerabilities">{t('security.vulnerabilities')}</TabsTrigger>
          <TabsTrigger value="licenses">{t('security.licenses')}</TabsTrigger>
          <TabsTrigger value="history">{t('security.scanHistory')}</TabsTrigger>
        </TabsList>

        <TabsContent value="vulnerabilities" className="space-y-6 mt-6">
          {/* Search and Filters */}
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
              <Input
                placeholder="Search vulnerabilities..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Select value={filterSeverity} onValueChange={setFilterSeverity}>
              <SelectTrigger className="w-full sm:w-40">
                <SelectValue placeholder="Severity" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Severities</SelectItem>
                <SelectItem value="critical">Critical</SelectItem>
                <SelectItem value="high">High</SelectItem>
                <SelectItem value="medium">Medium</SelectItem>
                <SelectItem value="low">Low</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Vulnerabilities List */}
          <div className="space-y-4">
            {filteredVulnerabilities.map((vuln) => (
              <Card key={vuln.id}>
                <CardContent className="p-6">
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <Badge className={severityBadgeColor(vuln.severity)}>
                          {severityIcon(vuln.severity)}
                          <span className="ml-1">{vuln.severity.toUpperCase()}</span>
                        </Badge>
                        <Badge variant="outline">Score: {vuln.score}</Badge>
                        <code className="text-sm bg-gray-100 dark:bg-gray-800 px-2 py-1 rounded">
                          {vuln.id}
                        </code>
                      </div>
                      <h3 className="text-lg text-gray-900 dark:text-white mb-2 font-medium">
                        {vuln.artifact}
                      </h3>
                      <p className="text-gray-600 dark:text-gray-400 mb-4">
                        {vuln.description}
                      </p>
                      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                        <div>
                          <p className="text-gray-600 dark:text-gray-400">Affected Versions</p>
                          <p className="text-gray-900 dark:text-white font-mono">{vuln.affectedVersions}</p>
                        </div>
                        <div>
                          <p className="text-gray-600 dark:text-gray-400">Fixed Versions</p>
                          <p className="text-gray-900 dark:text-white font-mono">{vuln.fixedVersions}</p>
                        </div>
                        <div>
                          <p className="text-gray-600 dark:text-gray-400">Published</p>
                          <p className="text-gray-900 dark:text-white">{vuln.publishedDate}</p>
                        </div>
                        <div>
                          <p className="text-gray-600 dark:text-gray-400">Repository</p>
                          <p className="text-gray-900 dark:text-white">{vuln.repository}</p>
                        </div>
                      </div>
                    </div>
                    <Button variant="outline" size="sm">
                      <ExternalLink className="mr-2 size-4" />
                      {t('security.references')}
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </TabsContent>

        <TabsContent value="licenses" className="space-y-4 mt-6">
          {licenseIssues.map((issue) => (
            <Card key={issue.id}>
              <CardContent className="p-6">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <Badge variant={issue.type === 'incompatible' ? 'destructive' : 'secondary'}>
                        {issue.type.toUpperCase()}
                      </Badge>
                      <Badge variant="outline">{issue.license}</Badge>
                    </div>
                    <h3 className="text-lg text-gray-900 dark:text-white mb-2 font-medium">
                      {issue.artifact}
                    </h3>
                    <p className="text-gray-600 dark:text-gray-400 mb-2">
                      {issue.description}
                    </p>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      Repository: {issue.repository}
                    </p>
                  </div>
                  <Button variant="outline" size="sm">
                    <FileText className="mr-2 size-4" />
                    View License
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </TabsContent>

        <TabsContent value="history" className="space-y-4 mt-6">
          {scanHistory.map((scan) => (
            <Card key={scan.id}>
              <CardContent className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <div>
                    <h3 className="text-lg text-gray-900 dark:text-white font-medium mb-1">
                      {scan.repository}
                    </h3>
                    <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                      <span className="flex items-center gap-1">
                        <Clock className="size-4" />
                        {scan.scanDate}
                      </span>
                      <span>Duration: {scan.duration}</span>
                    </div>
                  </div>
                  <Badge variant="outline" className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                    {scan.status}
                  </Badge>
                </div>
                <div className="grid grid-cols-4 gap-4">
                  <div className="text-center p-3 rounded-lg bg-red-50 dark:bg-red-900/10">
                    <p className="text-2xl text-red-600 dark:text-red-400 font-bold">
                      {scan.vulnerabilities.critical}
                    </p>
                    <p className="text-xs text-gray-600 dark:text-gray-400 mt-1">Critical</p>
                  </div>
                  <div className="text-center p-3 rounded-lg bg-orange-50 dark:bg-orange-900/10">
                    <p className="text-2xl text-orange-600 dark:text-orange-400 font-bold">
                      {scan.vulnerabilities.high}
                    </p>
                    <p className="text-xs text-gray-600 dark:text-gray-400 mt-1">High</p>
                  </div>
                  <div className="text-center p-3 rounded-lg bg-yellow-50 dark:bg-yellow-900/10">
                    <p className="text-2xl text-yellow-600 dark:text-yellow-400 font-bold">
                      {scan.vulnerabilities.medium}
                    </p>
                    <p className="text-xs text-gray-600 dark:text-gray-400 mt-1">Medium</p>
                  </div>
                  <div className="text-center p-3 rounded-lg bg-blue-50 dark:bg-blue-900/10">
                    <p className="text-2xl text-blue-600 dark:text-blue-400 font-bold">
                      {scan.vulnerabilities.low}
                    </p>
                    <p className="text-xs text-gray-600 dark:text-gray-400 mt-1">Low</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </TabsContent>
      </Tabs>
    </div>
  );
}
