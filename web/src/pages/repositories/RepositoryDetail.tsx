import { useState } from 'react';
import { 
  ArrowLeft, Package, Download, Upload, Shield, Settings, Activity, HardDrive, Clock, Globe, 
  Copy, Check, RefreshCw, Users, Key, Search, Calendar, Eye, BarChart3, TrendingUp, 
  AlertTriangle, CheckCircle, Database, GitBranch, Terminal, Trash2, ChevronLeft, ChevronRight
} from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Progress } from '@/components/ui/progress';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { toast } from 'sonner';
import { copyToClipboard } from '@/utils/clipboard';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { RepositoryAccessControl } from './RepositoryAccessControl';
import { RepositorySetupGuide } from './RepositorySetupGuide';
import { getRepositoryById } from '@/data/repositories';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Separator } from '@/components/ui/separator';
import { useNavigate, useParams } from 'react-router-dom';

// Format date to YYYY-MM-DD HH:mm:ss
const formatDateTime = (dateString: string): string => {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};


export function RepositoryDetail() {
  const { t } = useLanguage();
  const navigate = useNavigate();
  const { repositoryId } = useParams();
  const [copiedItem, setCopiedItem] = useState('');
  const [accessControlOpen, setAccessControlOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const onBack = () => {
    navigate('/repositories');
  };

  const onEdit = (id: string) => {
    navigate(`/repositories/configure/${id}`);
  };

  // Get repository data by ID
  const repository = getRepositoryById(repositoryId || '');

  // If repository not found, show error
  if (!repository) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="sm" onClick={onBack}>
              <ArrowLeft className="size-4" />
            </Button>
            <h1 className="text-3xl text-gray-900 dark:text-white">仓库未找到</h1>
          </div>
        </div>
        <Card>
          <CardContent className="py-16 text-center">
            <Package className="size-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600 dark:text-gray-400">
              未找到ID为 {repositoryId} 的仓库
            </p>
            <Button onClick={onBack} className="mt-4">返回列表</Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  const recentArtifacts = [
    {
      id: '1',
      name: 'spring-boot-starter-web',
      version: '3.2.0',
      groupId: 'org.springframework.boot',
      size: '2.4 MB',
      uploaded: '2 hours ago',
      downloads: 156,
    },
    {
      id: '2',
      name: 'jackson-databind',
      version: '2.16.0',
      groupId: 'com.fasterxml.jackson.core',
      size: '1.8 MB',
      uploaded: '5 hours ago',
      downloads: 234,
    },
    {
      id: '3',
      name: 'lombok',
      version: '1.18.30',
      groupId: 'org.projectlombok',
      size: '1.2 MB',
      uploaded: '1 day ago',
      downloads: 567,
    },
    {
      id: '4',
      name: 'slf4j-api',
      version: '2.0.9',
      groupId: 'org.slf4j',
      size: '64 KB',
      uploaded: '2 days ago',
      downloads: 892,
    },
    {
      id: '5',
      name: 'junit-jupiter',
      version: '5.10.1',
      groupId: 'org.junit.jupiter',
      size: '458 KB',
      uploaded: '3 days ago',
      downloads: 423,
    },
    {
      id: '6',
      name: 'guava',
      version: '32.1.3',
      groupId: 'com.google.guava',
      size: '3.1 MB',
      uploaded: '4 days ago',
      downloads: 678,
    },
    {
      id: '7',
      name: 'commons-lang3',
      version: '3.14.0',
      groupId: 'org.apache.commons',
      size: '640 KB',
      uploaded: '5 days ago',
      downloads: 534,
    },
    {
      id: '8',
      name: 'mysql-connector-java',
      version: '8.0.33',
      groupId: 'mysql',
      size: '2.3 MB',
      uploaded: '6 days ago',
      downloads: 445,
    },
    {
      id: '9',
      name: 'httpclient',
      version: '4.5.14',
      groupId: 'org.apache.httpcomponents',
      size: '780 KB',
      uploaded: '1 week ago',
      downloads: 398,
    },
    {
      id: '10',
      name: 'gson',
      version: '2.10.1',
      groupId: 'com.google.code.gson',
      size: '260 KB',
      uploaded: '1 week ago',
      downloads: 823,
    },
    {
      id: '11',
      name: 'logback-classic',
      version: '1.4.14',
      groupId: 'ch.qos.logback',
      size: '290 KB',
      uploaded: '1 week ago',
      downloads: 512,
    },
    {
      id: '12',
      name: 'mockito-core',
      version: '5.7.0',
      groupId: 'org.mockito',
      size: '3.5 MB',
      uploaded: '2 weeks ago',
      downloads: 347,
    },
    {
      id: '13',
      name: 'spring-security-web',
      version: '6.2.0',
      groupId: 'org.springframework.security',
      size: '850 KB',
      uploaded: '2 weeks ago',
      downloads: 289,
    },
    {
      id: '14',
      name: 'hibernate-core',
      version: '6.4.1',
      groupId: 'org.hibernate.orm',
      size: '7.2 MB',
      uploaded: '2 weeks ago',
      downloads: 456,
    },
    {
      id: '15',
      name: 'netty-all',
      version: '4.1.104',
      groupId: 'io.netty',
      size: '5.8 MB',
      uploaded: '2 weeks ago',
      downloads: 378,
    },
    {
      id: '16',
      name: 'kafka-clients',
      version: '3.6.1',
      groupId: 'org.apache.kafka',
      size: '4.2 MB',
      uploaded: '3 weeks ago',
      downloads: 267,
    },
    {
      id: '17',
      name: 'elasticsearch-rest-client',
      version: '8.11.3',
      groupId: 'org.elasticsearch.client',
      size: '950 KB',
      uploaded: '3 weeks ago',
      downloads: 198,
    },
    {
      id: '18',
      name: 'jedis',
      version: '5.1.0',
      groupId: 'redis.clients',
      size: '620 KB',
      uploaded: '3 weeks ago',
      downloads: 445,
    },
    {
      id: '19',
      name: 'jackson-datatype-jsr310',
      version: '2.16.0',
      groupId: 'com.fasterxml.jackson.datatype',
      size: '120 KB',
      uploaded: '1 month ago',
      downloads: 356,
    },
    {
      id: '20',
      name: 'micrometer-core',
      version: '1.12.1',
      groupId: 'io.micrometer',
      size: '1.1 MB',
      uploaded: '1 month ago',
      downloads: 234,
    },
    {
      id: '21',
      name: 'spring-boot-starter-data-jpa',
      version: '3.2.0',
      groupId: 'org.springframework.boot',
      size: '890 KB',
      uploaded: '1 month ago',
      downloads: 567,
    },
    {
      id: '22',
      name: 'postgresql',
      version: '42.7.1',
      groupId: 'org.postgresql',
      size: '1.3 MB',
      uploaded: '1 month ago',
      downloads: 478,
    },
    {
      id: '23',
      name: 'validation-api',
      version: '3.0.2',
      groupId: 'jakarta.validation',
      size: '95 KB',
      uploaded: '1 month ago',
      downloads: 389,
    },
    {
      id: '24',
      name: 'aspectjweaver',
      version: '1.9.21',
      groupId: 'org.aspectj',
      size: '2.1 MB',
      uploaded: '2 months ago',
      downloads: 234,
    },
    {
      id: '25',
      name: 'snakeyaml',
      version: '2.2',
      groupId: 'org.yaml',
      size: '340 KB',
      uploaded: '2 months ago',
      downloads: 512,
    },
  ];

  // Pagination calculations
  const totalPages = Math.ceil(recentArtifacts.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const paginatedArtifacts = recentArtifacts.slice(startIndex, endIndex);

  const activityLogs = [
    {
      id: '1',
      type: 'upload',
      user: 'john.doe@example.com',
      artifact: 'spring-boot-starter-web:3.2.0',
      timestamp: '2 hours ago',
      ip: '192.168.1.100',
    },
    {
      id: '2',
      type: 'download',
      user: 'jane.smith@example.com',
      artifact: 'jackson-databind:2.16.0',
      timestamp: '3 hours ago',
      ip: '192.168.1.105',
    },
    {
      id: '3',
      type: 'upload',
      user: 'bob.johnson@example.com',
      artifact: 'lombok:1.18.30',
      timestamp: '1 day ago',
      ip: '192.168.1.110',
    },
    {
      id: '4',
      type: 'download',
      user: 'alice.wilson@example.com',
      artifact: 'slf4j-api:2.0.9',
      timestamp: '1 day ago',
      ip: '192.168.1.115',
    },
    {
      id: '5',
      type: 'scan',
      user: 'system',
      artifact: 'All artifacts',
      timestamp: '2 days ago',
      ip: 'internal',
    },
  ];

  const downloadTrend = [
    { date: '2024-01-11', downloads: 234 },
    { date: '2024-01-12', downloads: 345 },
    { date: '2024-01-13', downloads: 456 },
    { date: '2024-01-14', downloads: 567 },
    { date: '2024-01-15', downloads: 432 },
    { date: '2024-01-16', downloads: 543 },
    { date: '2024-01-17', downloads: 654 },
  ];

  const handleCopyUrl = async () => {
    const success = await copyToClipboard(repository.url);
    if (success) {
      setCopiedItem('url');
      toast.success(t('artifacts.urlCopied'));
      setTimeout(() => setCopiedItem(''), 2000);
    } else {
      toast.error('Failed to copy URL');
    }
  };

  const handleCopyConfig = async () => {
    const config = JSON.stringify(repository.settings, null, 2);
    const success = await copyToClipboard(config);
    if (success) {
      setCopiedItem('config');
      toast.success(t('repositories.configCopied'));
      setTimeout(() => setCopiedItem(''), 2000);
    } else {
      toast.error('Failed to copy configuration');
    }
  };

  const handleScan = () => {
    toast.success(t('repositories.scanStarted'));
  };

  const handleRebuildIndex = () => {
    toast.success(t('repositories.indexRebuildStarted'));
  };

  const getStatusIcon = (status: string) => {
    if (status === 'online') return <CheckCircle className="size-5 text-green-500" />;
    return <Activity className="size-5 text-gray-400" />;
  };

  const getTypeIcon = (type: string) => {
    if (type === 'hosted') return <Database className="size-5 text-blue-500" />;
    if (type === 'proxy') return <Globe className="size-5 text-purple-500" />;
    if (type === 'group') return <GitBranch className="size-5 text-green-500" />;
    return <Package className="size-5 text-gray-500" />;
  };

  const formatBadgeColor = (format: string) => {
    const colors: Record<string, string> = {
      'Maven': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
      'Docker': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      'NPM': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
    };
    return colors[format] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  const getHealthColor = (score: number) => {
    if (score >= 95) return 'text-green-600 dark:text-green-400';
    if (score >= 80) return 'text-yellow-600 dark:text-yellow-400';
    return 'text-red-600 dark:text-red-400';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm" onClick={onBack}>
            <ArrowLeft className="size-4" />
          </Button>
          <div>
            <div className="flex items-center gap-3 mb-2">
              {getTypeIcon(repository.type)}
              <h1 className="text-3xl text-gray-900 dark:text-white">{repository.name}</h1>
              {getStatusIcon(repository.status)}
            </div>
            <p className="text-gray-600 dark:text-gray-400">{repository.description}</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" onClick={handleRebuildIndex}>
            <RefreshCw className="mr-2 size-4" />
            {t('repositories.rebuildIndex')}
          </Button>
          <Button variant="outline" size="sm" onClick={handleScan}>
            <Shield className="mr-2 size-4" />
            {t('repositories.scan')}
          </Button>
          <Button variant="outline" size="sm" onClick={() => onEdit(repository.id)}>
            <Settings className="mr-2 size-4" />
            {t('repositories.configure')}
          </Button>
        </div>
      </div>

      {/* Info Cards */}
      <div className="flex items-center gap-3 flex-wrap">
        <Badge className={formatBadgeColor(repository.format)} className="px-3 py-1">
          {repository.format}
        </Badge>
        <Badge variant="outline" className="px-3 py-1">
          {repository.type.toUpperCase()}
        </Badge>
        {repository.settings.public && (
          <Badge variant="outline" className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300 px-3 py-1">
            <Globe className="mr-1 size-3" />
            Public
          </Badge>
        )}
        {repository.settings.indexed && (
          <Badge variant="outline" className="bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300 px-3 py-1">
            <Search className="mr-1 size-3" />
            Indexed
          </Badge>
        )}
        <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 ml-auto">
          <Calendar className="size-4" />
          <span>{t('repositories.created')} {formatDateTime(repository.createdAt)}</span>
        </div>
      </div>

      {/* URL Box */}
      <Card className="bg-gray-50 dark:bg-gray-800/50">
        <CardContent className="pt-6">
          <div className="flex items-center justify-between">
            <div className="flex-1">
              <p className="text-xs text-gray-600 dark:text-gray-400 mb-1">{t('repositories.url')}</p>
              <p className="font-mono text-sm text-gray-900 dark:text-white">{repository.url}</p>
            </div>
            <Button variant="outline" size="sm" onClick={handleCopyUrl}>
              {copiedItem === 'url' ? (
                <>
                  <Check className="mr-2 size-4" />
                  Copied
                </>
              ) : (
                <>
                  <Copy className="mr-2 size-4" />
                  {t('repositories.copyUrl')}
                </>
              )}
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Statistics Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.artifacts')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {repository.artifacts.toLocaleString()}
                </p>
                <p className="text-xs text-green-600 dark:text-green-400 mt-1">
                  +{repository.stats.uploadsToday} today
                </p>
              </div>
              <div className="size-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                <Package className="size-6 text-blue-600 dark:text-blue-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.downloads')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {repository.stats.downloads.toLocaleString()}
                </p>
                <p className="text-xs text-green-600 dark:text-green-400 mt-1">
                  +{repository.stats.downloadsToday} today
                </p>
              </div>
              <div className="size-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                <Download className="size-6 text-green-600 dark:text-green-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.storageUsed')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {repository.size}
                </p>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                  24.5% of quota
                </p>
              </div>
              <div className="size-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                <HardDrive className="size-6 text-purple-600 dark:text-purple-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.healthScoreLabel')}</p>
                <p className={`text-2xl font-semibold mt-1 ${getHealthColor(repository.health.score)}`}>
                  {repository.health.score}%
                </p>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                  {repository.health.issues} {repository.health.issues === 1 ? 'issue' : 'issues'}
                </p>
              </div>
              <div className="size-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
                <Activity className="size-6 text-orange-600 dark:text-orange-400" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Main Content Tabs */}
      <Tabs defaultValue="artifacts" className="space-y-6">
        <TabsList>
          <TabsTrigger value="artifacts">
            <Package className="mr-2 size-4" />
            {t('repositories.artifactsTab')} ({repository.artifacts})
          </TabsTrigger>
          <TabsTrigger value="activity">
            <Activity className="mr-2 size-4" />
            {t('repositories.activityTab')}
          </TabsTrigger>
          <TabsTrigger value="analytics">
            <BarChart3 className="mr-2 size-4" />
            {t('repositories.analyticsTab')}
          </TabsTrigger>
          <TabsTrigger value="security">
            <Shield className="mr-2 size-4" />
            {t('repositories.securityTab')}
          </TabsTrigger>
          <TabsTrigger value="setup">
            <Terminal className="mr-2 size-4" />
            {t('repositories.setupTab')}
          </TabsTrigger>
          <TabsTrigger value="settings">
            <Settings className="mr-2 size-4" />
            {t('repositories.settingsTab')}
          </TabsTrigger>
        </TabsList>

        {/* Artifacts Tab */}
        <TabsContent value="artifacts" className="space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex-1 max-w-md relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
              <Input
                placeholder={t('repositories.searchArtifacts')}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <div className="flex items-center gap-2">
              <Select defaultValue="recent">
                <SelectTrigger className="w-40">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="recent">{t('repositories.recentlyUploaded')}</SelectItem>
                  <SelectItem value="popular">{t('repositories.mostDownloaded')}</SelectItem>
                  <SelectItem value="name">{t('repositories.sortByName')}</SelectItem>
                  <SelectItem value="size">{t('repositories.sortBySize')}</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <Card>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>{t('repositories.artifact')}</TableHead>
                  <TableHead>{t('repositories.version')}</TableHead>
                  <TableHead>{t('repositories.size')}</TableHead>
                  <TableHead>{t('repositories.downloads')}</TableHead>
                  <TableHead>{t('repositories.uploaded')}</TableHead>
                  <TableHead className="text-right">{t('repositories.actions')}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {paginatedArtifacts.map((artifact) => (
                  <TableRow key={artifact.id}>
                    <TableCell>
                      <div>
                        <p className="font-medium text-gray-900 dark:text-white">{artifact.name}</p>
                        <p className="text-xs text-gray-500 dark:text-gray-400">{artifact.groupId}</p>
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge variant="outline">{artifact.version}</Badge>
                    </TableCell>
                    <TableCell className="text-gray-600 dark:text-gray-400">{artifact.size}</TableCell>
                    <TableCell className="text-gray-600 dark:text-gray-400">{artifact.downloads}</TableCell>
                    <TableCell className="text-gray-600 dark:text-gray-400">{artifact.uploaded}</TableCell>
                    <TableCell className="text-right">
                      <div className="flex items-center justify-end gap-2">
                        <Button variant="ghost" size="sm">
                          <Download className="size-4" />
                        </Button>
                        <Button variant="ghost" size="sm">
                          <Eye className="size-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Card>

          {/* Pagination */}
          <div className="flex items-center justify-between mt-4">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(currentPage - 1)}
              disabled={currentPage === 1}
            >
              <ChevronLeft className="size-4" />
              {t('repositories.previous')}
            </Button>
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {currentPage} / {totalPages}
            </p>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(currentPage + 1)}
              disabled={currentPage === totalPages}
            >
              {t('repositories.next')}
              <ChevronRight className="size-4" />
            </Button>
          </div>
        </TabsContent>

        {/* Activity Tab */}
        <TabsContent value="activity" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>{t('repositories.recentActivity')}</CardTitle>
              <CardDescription>{t('repositories.repositoryEventsDesc')}</CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>{t('repositories.type')}</TableHead>
                    <TableHead>{t('repositories.user')}</TableHead>
                    <TableHead>{t('repositories.artifact')}</TableHead>
                    <TableHead>{t('repositories.ipAddress')}</TableHead>
                    <TableHead>{t('repositories.time')}</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {activityLogs.map((log) => (
                    <TableRow key={log.id}>
                      <TableCell>
                        <Badge variant={log.type === 'upload' ? 'default' : 'outline'}>
                          {log.type === 'upload' && <Upload className="mr-1 size-3" />}
                          {log.type === 'download' && <Download className="mr-1 size-3" />}
                          {log.type === 'scan' && <Shield className="mr-1 size-3" />}
                          {log.type}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-gray-900 dark:text-white">{log.user}</TableCell>
                      <TableCell className="text-gray-600 dark:text-gray-400 font-mono text-sm">{log.artifact}</TableCell>
                      <TableCell className="text-gray-600 dark:text-gray-400">{log.ip}</TableCell>
                      <TableCell className="text-gray-600 dark:text-gray-400">{log.timestamp}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Analytics Tab */}
        <TabsContent value="analytics" className="space-y-4">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>Download Trend</CardTitle>
                <CardDescription>Last 7 days</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {downloadTrend.map((item, index) => (
                    <div key={index} className="flex items-center gap-3">
                      <p className="text-xs text-gray-600 dark:text-gray-400 w-20">{item.date.slice(5)}</p>
                      <div className="flex-1">
                        <Progress value={(item.downloads / 700) * 100} className="h-2" />
                      </div>
                      <p className="text-sm text-gray-900 dark:text-white w-12 text-right">{item.downloads}</p>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Top Users</CardTitle>
                <CardDescription>By download count</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[
                    { user: 'john.doe@example.com', downloads: 456, avatar: 'JD' },
                    { user: 'jane.smith@example.com', downloads: 389, avatar: 'JS' },
                    { user: 'bob.johnson@example.com', downloads: 312, avatar: 'BJ' },
                    { user: 'alice.wilson@example.com', downloads: 267, avatar: 'AW' },
                    { user: 'charlie.brown@example.com', downloads: 234, avatar: 'CB' },
                  ].map((item, index) => (
                    <div key={index} className="flex items-center gap-3">
                      <div className="size-8 bg-blue-100 dark:bg-blue-900/30 rounded-full flex items-center justify-center text-xs font-medium text-blue-600 dark:text-blue-400">
                        {item.avatar}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm text-gray-900 dark:text-white truncate">{item.user}</p>
                      </div>
                      <Badge variant="secondary">{item.downloads}</Badge>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-sm text-gray-600 dark:text-gray-400">Total Bandwidth</p>
                  <TrendingUp className="size-4 text-green-500" />
                </div>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white">{repository.stats.bandwidth}</p>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">+12% from last week</p>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-sm text-gray-600 dark:text-gray-400">Unique Users</p>
                  <Users className="size-4 text-blue-500" />
                </div>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white">{repository.stats.uniqueUsers}</p>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">Active this week</p>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-sm text-gray-600 dark:text-gray-400">Avg Daily Downloads</p>
                  <BarChart3 className="size-4 text-purple-500" />
                </div>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white">
                  {Math.round(repository.stats.downloads / 30)}
                </p>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">Last 30 days</p>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        {/* Security Tab */}
        <TabsContent value="security" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Security Scan Status</CardTitle>
              <CardDescription>Last scan: {repository.security.lastScan}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {repository.security.vulnerabilities === 0 ? (
                <div className="flex items-center gap-3 p-4 bg-green-50 dark:bg-green-950/20 border border-green-200 dark:border-green-900 rounded-lg">
                  <CheckCircle className="size-6 text-green-600 dark:text-green-400" />
                  <div>
                    <p className="font-medium text-green-700 dark:text-green-300">No vulnerabilities found</p>
                    <p className="text-sm text-green-600 dark:text-green-400">All artifacts are secure</p>
                  </div>
                </div>
              ) : (
                <div className="flex items-center gap-3 p-4 bg-red-50 dark:bg-red-950/20 border border-red-200 dark:border-red-900 rounded-lg">
                  <AlertTriangle className="size-6 text-red-600 dark:text-red-400" />
                  <div className="flex-1">
                    <p className="font-medium text-red-700 dark:text-red-300">
                      {repository.security.vulnerabilities} vulnerabilities detected
                    </p>
                    <p className="text-sm text-red-600 dark:text-red-400">Action required</p>
                  </div>
                  <Button size="sm">View Details</Button>
                </div>
              )}

              <div className="grid grid-cols-4 gap-4 pt-4">
                <div className="text-center">
                  <p className="text-3xl font-semibold text-gray-900 dark:text-white">0</p>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">Critical</p>
                </div>
                <div className="text-center">
                  <p className="text-3xl font-semibold text-gray-900 dark:text-white">0</p>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">High</p>
                </div>
                <div className="text-center">
                  <p className="text-3xl font-semibold text-gray-900 dark:text-white">0</p>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">Medium</p>
                </div>
                <div className="text-center">
                  <p className="text-3xl font-semibold text-gray-900 dark:text-white">0</p>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">Low</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>{t('repositories.accessControlTitle')}</CardTitle>
                  <CardDescription>{t('repositories.accessControlDesc')}</CardDescription>
                </div>
                <Button onClick={() => setAccessControlOpen(true)}>
                  <Users className="mr-2 size-4" />
                  {t('repositories.manageAccess')}
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                <div className="flex items-center justify-between p-3 border border-gray-200 dark:border-gray-700 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900 dark:text-white">{t('repositories.publicAccess')}</p>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      {repository.settings.public ? t('repositories.publicAccessEnabled') : t('repositories.publicAccessDisabled')}
                    </p>
                  </div>
                  <Badge variant={repository.settings.public ? 'default' : 'secondary'}>
                    {repository.settings.public ? t('repositories.publicLabel') : t('repositories.privateLabel')}
                  </Badge>
                </div>
                <div className="p-3 border border-gray-200 dark:border-gray-700 rounded-lg bg-blue-50 dark:bg-blue-950/20">
                  <div className="flex items-center gap-2 mb-2">
                    <Users className="size-4 text-blue-600 dark:text-blue-400" />
                    <p className="font-medium text-blue-900 dark:text-blue-100">3 {t('access.users')}, 2 {t('access.teams')}, 3 {t('access.apiTokens')}</p>
                  </div>
                  <p className="text-sm text-blue-700 dark:text-blue-300">
                    Click "{t('repositories.manageAccess')}" to configure user permissions, team access, and API tokens.
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Setup Tab */}
        <TabsContent value="setup" className="space-y-4">
          <RepositorySetupGuide
            format={repository.format}
            repositoryName={repository.name}
            repositoryUrl={repository.url}
          />
        </TabsContent>

        {/* Settings Tab */}
        <TabsContent value="settings" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>{t('repositories.repositoryConfiguration')}</CardTitle>
              <CardDescription>{t('repositories.basicRepositorySettings')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('repositories.repositoryName')}</p>
                  <p className="text-gray-900 dark:text-white font-medium">{repository.name}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('repositories.repositoryFormat')}</p>
                  <p className="text-gray-900 dark:text-white font-medium">{repository.format}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('repositories.repositoryType')}</p>
                  <p className="text-gray-900 dark:text-white font-medium capitalize">{repository.type}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('repositories.blobStore')}</p>
                  <p className="text-gray-900 dark:text-white font-medium capitalize">{repository.settings.blobStore}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('repositories.created')}</p>
                  <p className="text-gray-900 dark:text-white font-medium">{formatDateTime(repository.createdAt)}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('repositories.lastUpdatedLabel')}</p>
                  <p className="text-gray-900 dark:text-white font-medium">{formatDateTime(repository.lastUpdated)}</p>
                </div>
              </div>

              <Separator />

              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('repositories.description')}</p>
                <p className="text-gray-900 dark:text-white">{repository.description}</p>
              </div>

              <Separator />

              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium text-gray-900 dark:text-white">{t('repositories.indexing')}</p>
                    <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.indexingEnableDesc')}</p>
                  </div>
                  <Badge variant={repository.settings.indexed ? 'default' : 'secondary'}>
                    {repository.settings.indexed ? t('repositories.enabled') : t('repositories.disabled')}
                  </Badge>
                </div>
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium text-gray-900 dark:text-white">{t('repositories.compression')}</p>
                    <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.compressionEnableDesc')}</p>
                  </div>
                  <Badge variant={repository.settings.compressionEnabled ? 'default' : 'secondary'}>
                    {repository.settings.compressionEnabled ? t('repositories.enabled') : t('repositories.disabled')}
                  </Badge>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="border-red-200 dark:border-red-900">
            <CardHeader>
              <CardTitle className="text-red-600 dark:text-red-400">{t('repositories.dangerZone')}</CardTitle>
              <CardDescription>{t('repositories.dangerZoneDesc')}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
                <div>
                  <p className="font-medium text-gray-900 dark:text-white">{t('repositories.clearAllArtifacts')}</p>
                  <p className="text-sm text-gray-600 dark:text-gray-400">{t('repositories.clearAllArtifactsDesc')}</p>
                </div>
                <Button variant="outline" size="sm">
                  {t('repositories.clear')}
                </Button>
              </div>
              <div className="flex items-center justify-between p-4 border border-red-200 dark:border-red-900 rounded-lg bg-red-50 dark:bg-red-950/20">
                <div>
                  <p className="font-medium text-red-700 dark:text-red-300">{t('repositories.deleteThisRepository')}</p>
                  <p className="text-sm text-red-600 dark:text-red-400">{t('repositories.deleteCannotBeUndone')}</p>
                </div>
                <Button variant="destructive" size="sm">
                  <Trash2 className="mr-2 size-4" />
                  {t('repositories.delete')}
                </Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Access Control Dialog */}
      <Dialog open={accessControlOpen} onOpenChange={setAccessControlOpen}>
        <DialogContent className="max-w-5xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>{t('access.title')}</DialogTitle>
            <DialogDescription>{t('access.description')}</DialogDescription>
          </DialogHeader>
          <RepositoryAccessControl 
            repositoryId={repository.id}
            repositoryName={repository.name}
          />
        </DialogContent>
      </Dialog>
    </div>
  );
}