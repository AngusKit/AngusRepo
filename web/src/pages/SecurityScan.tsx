import { useState } from 'react';
import { 
  Shield, AlertTriangle, CheckCircle, XCircle, Search, Filter,
  Clock, Package, ExternalLink, RefreshCw, Download, BarChart3,
  TrendingUp, TrendingDown, FileText, Lock, Unlock, ChevronRight,
  Zap, Info, Plus
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Progress } from '@/components/ui/progress';
import { Separator } from '@/components/ui/separator';
import { Pagination } from '@/components/ui/pagination';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';
import { ScanTaskDialog } from '@/components/ScanTaskDialog';

interface Vulnerability {
  id: string;
  cveId: string;
  title: string;
  severity: 'critical' | 'high' | 'medium' | 'low';
  score: number;
  artifact: string;
  version: string;
  repository: string;
  publishedDate: string;
  description: string;
  fixedVersions?: string[];
  references: string[];
}

interface ScanResult {
  id: string;
  repository: string;
  artifacts: number;
  status: 'scanning' | 'completed' | 'failed';
  progress: number;
  startTime: string;
  endTime?: string;
  vulnerabilities: {
    critical: number;
    high: number;
    medium: number;
    low: number;
  };
}

// Generate more mock data for pagination testing
const generateMockVulnerabilities = (): Vulnerability[] => {
  const base = [
    {
      id: '1',
      cveId: 'CVE-2024-1234',
      title: 'Remote Code Execution in Spring Framework',
      severity: 'critical' as const,
      score: 9.8,
      artifact: 'spring-core',
      version: '5.3.20',
      repository: 'maven-releases',
      publishedDate: '2024-01-15',
      description: 'A critical vulnerability allows remote attackers to execute arbitrary code through specially crafted requests.',
      fixedVersions: ['6.0.0', '5.3.30'],
      references: [
        'https://nvd.nist.gov/vuln/detail/CVE-2024-1234',
        'https://spring.io/security/cve-2024-1234'
      ]
    },
    {
      id: '2',
      cveId: 'CVE-2024-5678',
      title: 'SQL Injection in Jackson Databind',
      severity: 'high' as const,
      score: 8.1,
      artifact: 'jackson-databind',
      version: '2.14.0',
      repository: 'maven-releases',
      publishedDate: '2024-01-10',
      description: 'SQL injection vulnerability in deserialization process.',
      fixedVersions: ['2.15.0'],
      references: ['https://nvd.nist.gov/vuln/detail/CVE-2024-5678']
    },
    {
      id: '3',
      cveId: 'CVE-2024-9012',
      title: 'Cross-Site Scripting (XSS) in React',
      severity: 'medium' as const,
      score: 6.5,
      artifact: 'react',
      version: '17.0.2',
      repository: 'npm-public',
      publishedDate: '2024-01-05',
      description: 'XSS vulnerability in component rendering.',
      fixedVersions: ['18.0.0'],
      references: ['https://nvd.nist.gov/vuln/detail/CVE-2024-9012']
    },
    {
      id: '4',
      cveId: 'CVE-2024-3456',
      title: 'Buffer Overflow in Nginx',
      severity: 'high' as const,
      score: 7.5,
      artifact: 'nginx',
      version: '1.21.0',
      repository: 'docker-registry',
      publishedDate: '2023-12-20',
      description: 'Buffer overflow vulnerability in HTTP/2 implementation.',
      fixedVersions: ['1.25.0'],
      references: ['https://nvd.nist.gov/vuln/detail/CVE-2024-3456']
    }
  ];

  // Generate more entries for pagination
  const additional: Vulnerability[] = [];
  for (let i = 5; i <= 25; i++) {
    additional.push({
      id: i.toString(),
      cveId: `CVE-2024-${1000 + i}`,
      title: `Security Vulnerability ${i} in Package`,
      severity: ['critical', 'high', 'medium', 'low'][i % 4] as any,
      score: 5 + (i % 5),
      artifact: `package-${i}`,
      version: `1.${i}.0`,
      repository: ['maven-releases', 'docker-registry', 'npm-public'][i % 3],
      publishedDate: `2024-01-${String(i % 28 + 1).padStart(2, '0')}`,
      description: `This is a test vulnerability description for vulnerability ${i}.`,
      fixedVersions: [`2.${i}.0`],
      references: [`https://nvd.nist.gov/vuln/detail/CVE-2024-${1000 + i}`]
    });
  }

  return [...base, ...additional];
};

const generateMockScans = (): ScanResult[] => {
  const base = [
    {
      id: '1',
      repository: 'maven-releases',
      artifacts: 156,
      status: 'completed' as const,
      progress: 100,
      startTime: '2024-01-18T10:00:00Z',
      endTime: '2024-01-18T10:15:00Z',
      vulnerabilities: { critical: 2, high: 5, medium: 8, low: 12 }
    },
    {
      id: '2',
      repository: 'docker-registry',
      artifacts: 45,
      status: 'completed' as const,
      progress: 100,
      startTime: '2024-01-18T09:00:00Z',
      endTime: '2024-01-18T09:30:00Z',
      vulnerabilities: { critical: 0, high: 3, medium: 6, low: 4 }
    },
    {
      id: '3',
      repository: 'npm-public',
      artifacts: 89,
      status: 'scanning' as const,
      progress: 65,
      startTime: '2024-01-18T11:00:00Z',
      vulnerabilities: { critical: 0, high: 0, medium: 0, low: 0 }
    }
  ];

  // Generate more entries for pagination
  const additional: ScanResult[] = [];
  for (let i = 4; i <= 25; i++) {
    additional.push({
      id: i.toString(),
      repository: ['maven-releases', 'docker-registry', 'npm-public', 'pypi-public'][i % 4],
      artifacts: 20 + (i * 5),
      status: ['completed', 'completed', 'completed', 'failed'][i % 4] as any,
      progress: 100,
      startTime: `2024-01-${String(i % 28 + 1).padStart(2, '0')}T${String(i % 24).padStart(2, '0')}:00:00Z`,
      endTime: `2024-01-${String(i % 28 + 1).padStart(2, '0')}T${String((i % 24) + 1).padStart(2, '0')}:00:00Z`,
      vulnerabilities: {
        critical: i % 3,
        high: i % 5,
        medium: i % 7,
        low: i % 10
      }
    });
  }

  return [...base, ...additional];
};

const mockVulnerabilities = generateMockVulnerabilities();
const mockScanResults = generateMockScans();

export function SecurityScan() {
  const { t } = useLanguage();
  const [searchQuery, setSearchQuery] = useState('');
  const [severityFilter, setSeverityFilter] = useState('all');
  const [repositoryFilter, setRepositoryFilter] = useState('all');
  const [selectedVulnerability, setSelectedVulnerability] = useState<Vulnerability | null>(null);
  const [showExportPreview, setShowExportPreview] = useState(false);
  const [showTaskDialog, setShowTaskDialog] = useState(false);
  
  // Pagination for vulnerabilities
  const [vulnPage, setVulnPage] = useState(1);
  const vulnPerPage = 10;
  
  // Pagination for scans
  const [scanPage, setScanPage] = useState(1);
  const [scanSearchQuery, setScanSearchQuery] = useState('');
  const scanPerPage = 10;

  const filteredVulnerabilities = mockVulnerabilities.filter(vuln => {
    const matchesSearch = vuln.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         vuln.cveId.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         vuln.artifact.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesSeverity = severityFilter === 'all' || vuln.severity === severityFilter;
    const matchesRepo = repositoryFilter === 'all' || vuln.repository === repositoryFilter;
    return matchesSearch && matchesSeverity && matchesRepo;
  });

  const filteredScans = mockScanResults.filter(scan => {
    return scan.repository.toLowerCase().includes(scanSearchQuery.toLowerCase());
  });

  // Paginated vulnerabilities
  const totalVulnPages = Math.ceil(filteredVulnerabilities.length / vulnPerPage);
  const paginatedVulnerabilities = filteredVulnerabilities.slice(
    (vulnPage - 1) * vulnPerPage,
    vulnPage * vulnPerPage
  );

  // Paginated scans
  const totalScanPages = Math.ceil(filteredScans.length / scanPerPage);
  const paginatedScans = filteredScans.slice(
    (scanPage - 1) * scanPerPage,
    scanPage * scanPerPage
  );

  const totalVulnerabilities = mockVulnerabilities.length;
  const criticalCount = mockVulnerabilities.filter(v => v.severity === 'critical').length;
  const highCount = mockVulnerabilities.filter(v => v.severity === 'high').length;
  const mediumCount = mockVulnerabilities.filter(v => v.severity === 'medium').length;
  const lowCount = mockVulnerabilities.filter(v => v.severity === 'low').length;

  const getSeverityColor = (severity: string) => {
    const colors: Record<string, string> = {
      'critical': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
      'high': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
      'medium': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300',
      'low': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300'
    };
    return colors[severity] || colors.low;
  };

  const getSeverityIcon = (severity: string) => {
    if (severity === 'critical' || severity === 'high') {
      return <XCircle className="size-5" />;
    }
    return <AlertTriangle className="size-5" />;
  };

  return (
    <div className="flex flex-col h-[calc(100vh-2rem)]">
      {/* Header */}
      <div className="mb-6">
        <div className="mb-4">
          <h1 className="text-3xl text-gray-900 dark:text-white mb-2">
            {t('security.title')}
          </h1>
          <p className="text-gray-600 dark:text-gray-400">
            {t('security.scanDescription')}
          </p>
        </div>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-5 gap-4 mb-6">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-gray-600 dark:text-gray-400">{t('security.totalVulnerabilities')}</p>
              <Shield className="size-8 text-gray-400 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-gray-900 dark:text-white">{totalVulnerabilities}</p>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
              {t('security.across')} 6 {t('security.repositories')}
            </p>
          </CardContent>
        </Card>
        <Card className="border-red-200 dark:border-red-900">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-red-600 dark:text-red-400">{t('security.critical')}</p>
              <XCircle className="size-8 text-red-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-red-600 dark:text-red-400">{criticalCount}</p>
            <div className="flex items-center gap-1 mt-1">
              <TrendingDown className="size-3 text-green-600 dark:text-green-400" />
              <p className="text-sm text-green-600 dark:text-green-400">-2 {t('security.thisWeek')}</p>
            </div>
          </CardContent>
        </Card>
        <Card className="border-orange-200 dark:border-orange-900">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-orange-600 dark:text-orange-400">{t('security.high')}</p>
              <AlertTriangle className="size-8 text-orange-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-orange-600 dark:text-orange-400">{highCount}</p>
            <div className="flex items-center gap-1 mt-1">
              <TrendingUp className="size-3 text-red-600 dark:text-red-400" />
              <p className="text-sm text-red-600 dark:text-red-400">+1 {t('security.thisWeek')}</p>
            </div>
          </CardContent>
        </Card>
        <Card className="border-yellow-200 dark:border-yellow-900">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-yellow-600 dark:text-yellow-400">{t('security.medium')}</p>
              <Info className="size-8 text-yellow-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-yellow-600 dark:text-yellow-400">{mediumCount}</p>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">{t('security.stable')}</p>
          </CardContent>
        </Card>
        <Card className="border-blue-200 dark:border-blue-900">
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-blue-600 dark:text-blue-400">{t('security.low')}</p>
              <CheckCircle className="size-8 text-blue-500 opacity-20" />
            </div>
            <p className="text-3xl font-bold text-blue-600 dark:text-blue-400">{lowCount}</p>
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">{t('security.lowRisk')}</p>
          </CardContent>
        </Card>
      </div>

      {/* Main Content */}
      <div className="flex-1 min-h-0">
        <Tabs defaultValue="vulnerabilities" className="h-full flex flex-col">
          <TabsList>
            <TabsTrigger value="vulnerabilities">
              <AlertTriangle className="mr-2 size-4" />
              {t('security.vulnerabilities')}
            </TabsTrigger>
            <TabsTrigger value="scans">
              <RefreshCw className="mr-2 size-4" />
              {t('security.scans')}
            </TabsTrigger>
            <TabsTrigger value="licenses">
              <FileText className="mr-2 size-4" />
              {t('security.licenses')}
            </TabsTrigger>
          </TabsList>

          {/* Vulnerabilities Tab */}
          <TabsContent value="vulnerabilities" className="flex-1 flex flex-col space-y-4 min-h-0">
            {/* Search and Filters */}
            <div className="flex items-center gap-4">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
                <Input
                  placeholder={t('security.searchVulnerabilities')}
                  value={searchQuery}
                  onChange={(e) => {
                    setSearchQuery(e.target.value);
                    setVulnPage(1); // Reset to first page when searching
                  }}
                  className="pl-10"
                />
              </div>
              <Select value={severityFilter} onValueChange={(v) => { setSeverityFilter(v); setVulnPage(1); }}>
                <SelectTrigger className="w-48">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">{t('security.allSeverities')}</SelectItem>
                  <SelectItem value="critical">{t('security.critical')}</SelectItem>
                  <SelectItem value="high">{t('security.high')}</SelectItem>
                  <SelectItem value="medium">{t('security.medium')}</SelectItem>
                  <SelectItem value="low">{t('security.low')}</SelectItem>
                </SelectContent>
              </Select>
              <Select value={repositoryFilter} onValueChange={(v) => { setRepositoryFilter(v); setVulnPage(1); }}>
                <SelectTrigger className="w-48">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">{t('artifacts.allRepositories')}</SelectItem>
                  <SelectItem value="maven-releases">maven-releases</SelectItem>
                  <SelectItem value="docker-registry">docker-registry</SelectItem>
                  <SelectItem value="npm-public">npm-public</SelectItem>
                </SelectContent>
              </Select>
              
              <Separator orientation="vertical" className="h-8" />
              
              <Button variant="outline" onClick={() => setShowExportPreview(true)}>
                <Download className="mr-2 size-4" />
                {t('security.exportReport')}
              </Button>
            </div>

            {/* Vulnerability List */}
            <Card className="flex-1 min-h-0 flex flex-col">
              <div className="flex-1 overflow-y-auto">
                <div className="p-6 space-y-3">
                  {paginatedVulnerabilities.map((vuln) => (
                    <div 
                      key={vuln.id}
                      className="p-4 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors cursor-pointer"
                      onClick={() => setSelectedVulnerability(vuln)}
                    >
                      <div className="flex items-start justify-between gap-4">
                        <div className="flex-1">
                          <div className="flex items-center gap-3 mb-2">
                            <Badge className={getSeverityColor(vuln.severity)}>
                              {getSeverityIcon(vuln.severity)}
                              <span className="ml-1">{vuln.severity.toUpperCase()}</span>
                            </Badge>
                            <Badge variant="outline" className="font-mono text-xs">
                              {vuln.cveId}
                            </Badge>
                            <Badge variant="secondary" className="text-xs">
                              CVSS: {vuln.score}
                            </Badge>
                          </div>
                          <h3 className="font-semibold text-gray-900 dark:text-white mb-2">
                            {vuln.title}
                          </h3>
                          <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                            {vuln.description}
                          </p>
                          <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                            <div className="flex items-center gap-1">
                              <Package className="size-4" />
                              <span>{vuln.artifact} v{vuln.version}</span>
                            </div>
                            <div className="flex items-center gap-1">
                              <Clock className="size-4" />
                              <span>{new Date(vuln.publishedDate).toLocaleDateString()}</span>
                            </div>
                            {vuln.fixedVersions && vuln.fixedVersions.length > 0 && (
                              <div className="flex items-center gap-1 text-green-600 dark:text-green-400">
                                <CheckCircle className="size-4" />
                                <span>{t('security.fixAvailable')}: {vuln.fixedVersions.join(', ')}</span>
                              </div>
                            )}
                          </div>
                        </div>
                        <Button variant="ghost" size="sm">
                          <ChevronRight className="size-4" />
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
              
              {/* Pagination */}
              {totalVulnPages > 1 && (
                <div className="p-4 border-t border-gray-200 dark:border-gray-700">
                  <Pagination
                    currentPage={vulnPage}
                    totalPages={totalVulnPages}
                    onPageChange={setVulnPage}
                  />
                </div>
              )}
            </Card>
          </TabsContent>

          {/* Scans Tab (renamed from Scan History) */}
          <TabsContent value="scans" className="flex-1 flex flex-col space-y-4 min-h-0">
            <div className="flex items-center gap-4">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
                <Input
                  placeholder={t('security.searchScans')}
                  value={scanSearchQuery}
                  onChange={(e) => {
                    setScanSearchQuery(e.target.value);
                    setScanPage(1); // Reset to first page when searching
                  }}
                  className="pl-10"
                />
              </div>
              <Button onClick={() => setShowTaskDialog(true)}>
                <Plus className="mr-2 size-4" />
                {t('security.createTask')}
              </Button>
            </div>
            
            <Card className="flex-1 min-h-0 flex flex-col">
              <CardHeader>
                <CardTitle>{t('security.recentScans')}</CardTitle>
                <CardDescription>{t('security.recentScansDesc')}</CardDescription>
              </CardHeader>
              <div className="flex-1 overflow-y-auto">
                <CardContent>
                  <div className="space-y-4">
                    {paginatedScans.map((scan) => (
                      <div key={scan.id} className="p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                        <div className="flex items-center justify-between mb-3">
                          <div className="flex items-center gap-3">
                            <div className={`size-10 rounded-full flex items-center justify-center ${
                              scan.status === 'completed' 
                                ? 'bg-green-100 dark:bg-green-900/30' 
                                : scan.status === 'scanning'
                                ? 'bg-blue-100 dark:bg-blue-900/30'
                                : 'bg-red-100 dark:bg-red-900/30'
                            }`}>
                              {scan.status === 'completed' && <CheckCircle className="size-5 text-green-600 dark:text-green-400" />}
                              {scan.status === 'scanning' && <RefreshCw className="size-5 text-blue-600 dark:text-blue-400 animate-spin" />}
                              {scan.status === 'failed' && <XCircle className="size-5 text-red-600 dark:text-red-400" />}
                            </div>
                            <div>
                              <h4 className="font-semibold text-gray-900 dark:text-white">
                                {scan.repository}
                              </h4>
                              <p className="text-sm text-gray-600 dark:text-gray-400">
                                {scan.artifacts} {t('security.artifactsScanned')} • {new Date(scan.startTime).toLocaleString()}
                              </p>
                            </div>
                          </div>
                          <div className="flex items-center gap-2">
                            {scan.status === 'completed' && (
                              <>
                                <Badge variant="destructive">{scan.vulnerabilities.critical} {t('security.critical')}</Badge>
                                <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300">
                                  {scan.vulnerabilities.high} {t('security.high')}
                                </Badge>
                                <Badge className="bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300">
                                  {scan.vulnerabilities.medium} {t('security.medium')}
                                </Badge>
                              </>
                            )}
                          </div>
                        </div>
                        {scan.status === 'scanning' && (
                          <div className="space-y-2">
                            <div className="flex items-center justify-between text-sm">
                              <span className="text-gray-600 dark:text-gray-400">{t('security.scanning')}...</span>
                              <span className="text-gray-900 dark:text-white font-medium">{scan.progress}%</span>
                            </div>
                            <Progress value={scan.progress} />
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </CardContent>
              </div>
              
              {/* Pagination */}
              {totalScanPages > 1 && (
                <div className="p-4 border-t border-gray-200 dark:border-gray-700">
                  <Pagination
                    currentPage={scanPage}
                    totalPages={totalScanPages}
                    onPageChange={setScanPage}
                  />
                </div>
              )}
            </Card>
          </TabsContent>

          {/* Licenses Tab */}
          <TabsContent value="licenses" className="flex-1 space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>{t('security.licenseCompliance')}</CardTitle>
                <CardDescription>{t('security.licenseComplianceDesc')}</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {['Apache-2.0', 'MIT', 'BSD-3-Clause', 'GPL-3.0', 'LGPL-2.1'].map((license, idx) => (
                    <div key={license} className="flex items-center gap-3 p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                      {idx < 3 ? (
                        <Unlock className="size-5 text-green-600 dark:text-green-400" />
                      ) : (
                        <Lock className="size-5 text-orange-600 dark:text-orange-400" />
                      )}
                      <div>
                        <h4 className="font-medium text-gray-900 dark:text-white">{license}</h4>
                        <p className="text-sm text-gray-600 dark:text-gray-400">
                          {Math.floor(Math.random() * 50) + 10} {t('security.artifacts')}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      {/* Vulnerability Detail Modal */}
      {selectedVulnerability && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => setSelectedVulnerability(null)}>
          <Card className="w-full max-w-2xl" onClick={(e) => e.stopPropagation()}>
            <CardHeader>
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-3">
                    <Badge className={getSeverityColor(selectedVulnerability.severity)}>
                      {selectedVulnerability.severity.toUpperCase()}
                    </Badge>
                    <Badge variant="outline" className="font-mono">
                      {selectedVulnerability.cveId}
                    </Badge>
                    <Badge variant="secondary">
                      CVSS: {selectedVulnerability.score}
                    </Badge>
                  </div>
                  <CardTitle className="text-xl">{selectedVulnerability.title}</CardTitle>
                </div>
                <Button variant="ghost" size="sm" onClick={() => setSelectedVulnerability(null)}>
                  <XCircle className="size-5" />
                </Button>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <h4 className="font-medium text-gray-900 dark:text-white mb-2">{t('security.description')}</h4>
                <p className="text-gray-600 dark:text-gray-400">{selectedVulnerability.description}</p>
              </div>
              <Separator />
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('security.affectedArtifact')}</p>
                  <p className="font-medium text-gray-900 dark:text-white">
                    {selectedVulnerability.artifact} v{selectedVulnerability.version}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('security.publishedDate')}</p>
                  <p className="font-medium text-gray-900 dark:text-white">
                    {new Date(selectedVulnerability.publishedDate).toLocaleDateString()}
                  </p>
                </div>
              </div>
              {selectedVulnerability.fixedVersions && selectedVulnerability.fixedVersions.length > 0 && (
                <>
                  <Separator />
                  <div>
                    <h4 className="font-medium text-gray-900 dark:text-white mb-2">{t('security.fixedVersions')}</h4>
                    <div className="flex gap-2">
                      {selectedVulnerability.fixedVersions.map((version) => (
                        <Badge key={version} className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                          {version}
                        </Badge>
                      ))}
                    </div>
                  </div>
                </>
              )}
              <Separator />
              <div>
                <h4 className="font-medium text-gray-900 dark:text-white mb-2">{t('security.references')}</h4>
                <div className="space-y-2">
                  {selectedVulnerability.references.map((ref, idx) => (
                    <a 
                      key={idx}
                      href={ref}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="flex items-center gap-2 text-sm text-blue-600 dark:text-blue-400 hover:underline"
                    >
                      <ExternalLink className="size-4" />
                      {ref}
                    </a>
                  ))}
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Export Preview Modal */}
      {showExportPreview && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => setShowExportPreview(false)}>
          <Card className="w-full max-w-2xl" onClick={(e) => e.stopPropagation()}>
            <CardHeader>
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <CardTitle className="text-xl">{t('security.exportReport')}</CardTitle>
                </div>
                <Button variant="ghost" size="sm" onClick={() => setShowExportPreview(false)}>
                  <XCircle className="size-5" />
                </Button>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <h4 className="font-medium text-gray-900 dark:text-white mb-2">{t('security.exportFormat')}</h4>
                <div className="flex items-center gap-4">
                  <Badge className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300">
                    PDF
                  </Badge>
                  <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                    CSV
                  </Badge>
                  <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300">
                    JSON
                  </Badge>
                </div>
              </div>
              <Separator />
              <div>
                <h4 className="font-medium text-gray-900 dark:text-white mb-2">{t('security.exportOptions')}</h4>
                <div className="flex items-center gap-4">
                  <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300">
                    {t('security.allVulnerabilities')}
                  </Badge>
                  <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300">
                    {t('security.critical')}
                  </Badge>
                  <Badge className="bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300">
                    {t('security.high')}
                  </Badge>
                </div>
              </div>
              <Separator />
              <div>
                <h4 className="font-medium text-gray-900 dark:text-white mb-2">{t('security.exportPreview')}</h4>
                <div className="space-y-2">
                  <div className="p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center gap-3">
                        <div className="size-10 rounded-full flex items-center justify-center bg-green-100 dark:bg-green-900/30">
                          <CheckCircle className="size-5 text-green-600 dark:text-green-400" />
                        </div>
                        <div>
                          <h4 className="font-semibold text-gray-900 dark:text-white">
                            maven-releases
                          </h4>
                          <p className="text-sm text-gray-600 dark:text-gray-400">
                            156 {t('security.artifactsScanned')} • {new Date('2024-01-18T10:00:00Z').toLocaleString()}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <Badge variant="destructive">2 {t('security.critical')}</Badge>
                        <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300">
                          5 {t('security.high')}
                        </Badge>
                        <Badge className="bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300">
                          8 {t('security.medium')}
                        </Badge>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <Separator />
              <div className="flex justify-end gap-2">
                <Button variant="outline" onClick={() => setShowExportPreview(false)}>
                  {t('security.cancel')}
                </Button>
                <Button onClick={() => { setShowExportPreview(false); toast.success(t('security.exportSuccess')); }}>
                  <Download className="mr-2 size-4" />
                  {t('security.export')}
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Scan Task Dialog */}
      {showTaskDialog && (
        <ScanTaskDialog onClose={() => setShowTaskDialog(false)} />
      )}
    </div>
  );
}