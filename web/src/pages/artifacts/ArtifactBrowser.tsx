import { useState } from 'react';
import { 
  Search, Download, Package, Upload, Trash2, Copy, Eye, Tag, 
  FileText, Folder, ChevronRight, ChevronDown, Home, Star, 
  Shield, AlertTriangle, CheckCircle, Clock, User, Hash, 
  Grid3x3, List, Filter, MoreVertical, ExternalLink, GitBranch,
  HardDrive, Calendar, Archive, FileCode, Image, File, Settings,
  TrendingUp, Database, Box, Layers, Code2, Terminal, Container, 
  Cpu, Globe, BookOpen, History, ChevronLeft, X, ArrowUpRight,
  Maximize2, Minimize2, SlidersHorizontal, BarChart3, ArrowLeft
} from 'lucide-react';
import { Button } from '@/components/ui/button';  
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger, DropdownMenuCheckboxItem } from '@/components/ui/dropdown-menu';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';
import { copyToClipboard } from '@/utils/clipboard';

interface ArtifactItem {
  id: string;
  name: string;
  path: string;
  format: 'Maven' | 'Docker' | 'NPM' | 'PyPI' | 'NuGet' | 'Raw';
  repository: string;
  version: string;
  description?: string;
  size: string;
  sizeBytes: number;
  downloads: number;
  stars: number;
  modified: string;
  modifiedBy: string;
  license?: string;
  tags?: string[];
  isLatest?: boolean;
  versions?: string[];
  vulnerability?: {
    critical: number;
    high: number;
    medium: number;
    low: number;
  };
}

// Helper functions - moved outside component
const getFormatIcon = (format: string) => {
  switch (format) {
    case 'Maven': return <Archive className="size-5 text-purple-500" />;
    case 'Docker': return <Container className="size-5 text-blue-500" />;
    case 'NPM': return <Box className="size-5 text-red-500" />;
    case 'PyPI': return <Package className="size-5 text-orange-500" />;
    case 'NuGet': return <Box className="size-5 text-indigo-500" />;
    default: return <Package className="size-5 text-gray-500" />;
  }
};

const formatBadgeColor = (format: string) => {
  const colors: Record<string, string> = {
    'Maven': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
    'Docker': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
    'NPM': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
    'PyPI': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
    'NuGet': 'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-300',
  };
  return colors[format] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
};

const getVulnerabilityBadge = (vuln?: { critical: number; high: number; medium: number; low: number }, t?: any) => {
  if (!vuln) return null;
  const total = vuln.critical + vuln.high + vuln.medium + vuln.low;
  if (total === 0) {
    return (
      <Badge className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
        <CheckCircle className="mr-1 size-3" />
        {t ? t('artifacts.secure') : 'Secure'}
      </Badge>
    );
  }
  if (vuln.critical > 0) {
    return (
      <Badge className="bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300">
        <AlertTriangle className="mr-1 size-3" />
        {vuln.critical} {t ? t('artifacts.critical') : 'Critical'}
      </Badge>
    );
  }
  if (vuln.high > 0) {
    return (
      <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300">
        <AlertTriangle className="mr-1 size-3" />
        {vuln.high} {t ? t('artifacts.high') : 'High'}
      </Badge>
    );
  }
  return (
    <Badge className="bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300">
      {total} {t ? t('artifacts.issues') : 'Issues'}
    </Badge>
  );
};

// Mock data
const mockArtifacts: ArtifactItem[] = [
  {
    id: '1',
    name: 'spring-core',
    path: 'org/springframework/spring-core',
    format: 'Maven',
    repository: 'maven-releases',
    version: '6.1.0',
    description: 'Spring Core Framework - Core utilities and abstractions',
    size: '1.5 MB',
    sizeBytes: 1572864,
    downloads: 125430,
    stars: 89,
    modified: '2024-01-15T10:30:00Z',
    modifiedBy: 'spring-bot',
    license: 'Apache-2.0',
    tags: ['framework', 'spring', 'core'],
    isLatest: true,
    versions: ['6.1.0', '6.0.14', '6.0.13', '5.3.31'],
    vulnerability: { critical: 0, high: 0, medium: 0, low: 0 }
  },
  {
    id: '2',
    name: 'nginx',
    path: 'docker/nginx',
    format: 'Docker',
    repository: 'docker-registry',
    version: '1.25-alpine',
    description: 'Official Nginx Docker image - High performance web server',
    size: '142 MB',
    sizeBytes: 148897792,
    downloads: 567892,
    stars: 234,
    modified: '2024-01-14T15:20:00Z',
    modifiedBy: 'nginx-team',
    license: 'BSD-2-Clause',
    tags: ['webserver', 'proxy', 'official'],
    isLatest: true,
    versions: ['1.25-alpine', '1.24-alpine', '1.23-alpine'],
    vulnerability: { critical: 0, high: 1, medium: 2, low: 0 }
  },
  {
    id: '3',
    name: 'react',
    path: 'npm/react',
    format: 'NPM',
    repository: 'npm-public',
    version: '18.2.0',
    description: 'React is a JavaScript library for building user interfaces',
    size: '2.8 MB',
    sizeBytes: 2936012,
    downloads: 2341567,
    stars: 456,
    modified: '2024-01-13T09:15:00Z',
    modifiedBy: 'react-core',
    license: 'MIT',
    tags: ['ui', 'framework', 'javascript'],
    isLatest: true,
    versions: ['18.2.0', '18.1.0', '18.0.0', '17.0.2'],
    vulnerability: { critical: 0, high: 0, medium: 0, low: 0 }
  },
  {
    id: '4',
    name: 'pandas',
    path: 'pypi/pandas',
    format: 'PyPI',
    repository: 'pypi-public',
    version: '2.1.4',
    description: 'Powerful data structures for data analysis and manipulation',
    size: '18.5 MB',
    sizeBytes: 19398656,
    downloads: 892345,
    stars: 178,
    modified: '2024-01-12T14:45:00Z',
    modifiedBy: 'pandas-dev',
    license: 'BSD-3-Clause',
    tags: ['data', 'analysis', 'python'],
    isLatest: true,
    versions: ['2.1.4', '2.1.3', '2.1.2', '2.0.3'],
    vulnerability: { critical: 0, high: 0, medium: 1, low: 0 }
  },
  {
    id: '5',
    name: 'jackson-databind',
    path: 'com/fasterxml/jackson-databind',
    format: 'Maven',
    repository: 'maven-releases',
    version: '2.16.1',
    description: 'General data-binding functionality for Jackson',
    size: '1.6 MB',
    sizeBytes: 1677721,
    downloads: 98765,
    stars: 45,
    modified: '2024-01-10T08:20:00Z',
    modifiedBy: 'jackson-bot',
    license: 'Apache-2.0',
    tags: ['json', 'serialization', 'jackson'],
    isLatest: true,
    versions: ['2.16.1', '2.16.0', '2.15.3'],
    vulnerability: { critical: 0, high: 0, medium: 0, low: 0 }
  },
  {
    id: '6',
    name: 'lodash',
    path: 'npm/lodash',
    format: 'NPM',
    repository: 'npm-public',
    version: '4.17.21',
    description: 'Lodash modular utilities',
    size: '1.4 MB',
    sizeBytes: 1468006,
    downloads: 5678901,
    stars: 678,
    modified: '2024-01-09T16:30:00Z',
    modifiedBy: 'lodash-team',
    license: 'MIT',
    tags: ['utility', 'javascript', 'functional'],
    isLatest: true,
    versions: ['4.17.21', '4.17.20', '4.17.19'],
    vulnerability: { critical: 0, high: 0, medium: 0, low: 0 }
  }
];

export function ArtifactBrowser() {
  const { t } = useLanguage();
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('list');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRepository, setSelectedRepository] = useState('all');
  const [selectedFormat, setSelectedFormat] = useState('all');
  const [selectedArtifact, setSelectedArtifact] = useState<ArtifactItem | null>(null);
  const [sortBy, setSortBy] = useState<'name' | 'downloads' | 'modified'>('downloads');
  const [showFilters, setShowFilters] = useState(false);
  const [viewFilter, setViewFilter] = useState<'all' | 'favorites' | 'watching'>('all');

  // Filter artifacts
  const filteredArtifacts = mockArtifacts.filter(artifact => {
    const matchesSearch = artifact.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         artifact.description?.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesRepo = selectedRepository === 'all' || artifact.repository === selectedRepository;
    const matchesFormat = selectedFormat === 'all' || artifact.format === selectedFormat;
    
    // Filter by view type (all/favorites/watching)
    let matchesViewFilter = true;
    if (viewFilter === 'favorites') {
      // Simulate favorites: first 30% of artifacts
      const favoriteIds = mockArtifacts.slice(0, Math.ceil(mockArtifacts.length * 0.3)).map(a => a.id);
      matchesViewFilter = favoriteIds.includes(artifact.id);
    } else if (viewFilter === 'watching') {
      // Simulate watching: first 20% of artifacts
      const watchingIds = mockArtifacts.slice(0, Math.ceil(mockArtifacts.length * 0.2)).map(a => a.id);
      matchesViewFilter = watchingIds.includes(artifact.id);
    }
    
    return matchesSearch && matchesRepo && matchesFormat && matchesViewFilter;
  });

  // Sort artifacts
  const sortedArtifacts = [...filteredArtifacts].sort((a, b) => {
    switch (sortBy) {
      case 'name':
        return a.name.localeCompare(b.name);
      case 'downloads':
        return b.downloads - a.downloads;
      case 'modified':
        return new Date(b.modified).getTime() - new Date(a.modified).getTime();
      default:
        return 0;
    }
  });

  const handleCopy = async (text: string, label: string) => {
    const success = await copyToClipboard(text);
    if (success) {
      toast.success(`${label} ${t('artifacts.copiedToClipboard')}`);
    }
  };

  // If artifact is selected, show detail page
  if (selectedArtifact) {
    return (
      <ArtifactDetailPage
        artifact={selectedArtifact}
        onBack={() => setSelectedArtifact(null)}
        onCopy={handleCopy}
      />
    );
  }

  // Main artifact list view
  return (
    <div className="flex flex-col h-[calc(100vh-8rem)]">
      {/* Top Bar */}
      <div className="mb-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-3xl text-gray-900 dark:text-white mb-2">
              {t('artifacts.browserTitle')}
            </h1>
            <p className="text-gray-600 dark:text-gray-400">
              {t('artifacts.browserDescription')}
            </p>
          </div>
        </div>

        {/* Stats Bar - Moved above search */}
        <div className="grid grid-cols-4 gap-4 mb-6">
          <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => setViewFilter('all')}>
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    {t('artifacts.totalArtifacts')}
                  </p>
                  <p className="text-2xl font-semibold text-gray-900 dark:text-white">
                    {mockArtifacts.length}
                  </p>
                </div>
                <Package className={`size-8 opacity-20 ${viewFilter === 'all' ? 'text-blue-600' : 'text-blue-500'}`} />
              </div>
            </CardContent>
          </Card>
          <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => setViewFilter('favorites')}>
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    {t('artifacts.myFavorites')}
                  </p>
                  <p className="text-2xl font-semibold text-gray-900 dark:text-white">
                    {Math.floor(mockArtifacts.length * 0.3)}
                  </p>
                </div>
                <Star className={`size-8 opacity-20 ${viewFilter === 'favorites' ? 'text-yellow-600' : 'text-yellow-500'}`} />
              </div>
            </CardContent>
          </Card>
          <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => setViewFilter('watching')}>
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    {t('artifacts.watching')}
                  </p>
                  <p className="text-2xl font-semibold text-gray-900 dark:text-white">
                    {Math.floor(mockArtifacts.length * 0.2)}
                  </p>
                </div>
                <Eye className={`size-8 opacity-20 ${viewFilter === 'watching' ? 'text-green-600' : 'text-green-500'}`} />
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">
                    {t('artifacts.totalStorage')}
                  </p>
                  <p className="text-2xl font-semibold text-gray-900 dark:text-white">
                    {(mockArtifacts.reduce((sum, a) => sum + a.sizeBytes, 0) / 1024 / 1024 / 1024).toFixed(1)} GB
                  </p>
                </div>
                <HardDrive className="size-8 text-purple-500 opacity-20" />
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Search and Filter Bar */}
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-5 text-gray-400" />
            <Input
              placeholder={t('artifacts.searchPlaceholder')}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-11 h-11 text-base"
            />
          </div>
          
          <div className="flex items-center gap-2">
            <Select value={selectedRepository} onValueChange={setSelectedRepository}>
              <SelectTrigger className="w-48 h-11">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{t('artifacts.allRepositories')}</SelectItem>
                <SelectItem value="maven-releases">maven-releases</SelectItem>
                <SelectItem value="docker-registry">docker-registry</SelectItem>
                <SelectItem value="npm-public">npm-public</SelectItem>
                <SelectItem value="pypi-public">pypi-public</SelectItem>
              </SelectContent>
            </Select>

            <Select value={selectedFormat} onValueChange={setSelectedFormat}>
              <SelectTrigger className="w-40 h-11">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{t('artifacts.allFormats')}</SelectItem>
                <SelectItem value="Maven">Maven</SelectItem>
                <SelectItem value="Docker">Docker</SelectItem>
                <SelectItem value="NPM">NPM</SelectItem>
                <SelectItem value="PyPI">PyPI</SelectItem>
                <SelectItem value="NuGet">NuGet</SelectItem>
              </SelectContent>
            </Select>

            <Button 
              variant={showFilters ? 'default' : 'outline'} 
              size="sm"
              className="h-11"
              onClick={() => setShowFilters(!showFilters)}
            >
              <SlidersHorizontal className="size-4" />
            </Button>

            <Separator orientation="vertical" className="h-8" />

            <Button
              variant={viewMode === 'grid' ? 'default' : 'outline'}
              size="sm"
              className="h-11"
              onClick={() => setViewMode('grid')}
            >
              <Grid3x3 className="size-4" />
            </Button>
            <Button
              variant={viewMode === 'list' ? 'default' : 'outline'}
              size="sm"
              className="h-11"
              onClick={() => setViewMode('list')}
            >
              <List className="size-4" />
            </Button>
          </div>
        </div>

        {/* Advanced Filters */}
        {showFilters && (
          <Card className="mt-4">
            <CardContent className="pt-6">
              <div className="grid grid-cols-4 gap-4">
                <div>
                  <label className="text-sm text-gray-600 dark:text-gray-400 mb-2 block">
                    {t('artifacts.license')}
                  </label>
                  <Select>
                    <SelectTrigger>
                      <SelectValue placeholder={t('artifacts.anyLicense')} />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="apache">Apache-2.0</SelectItem>
                      <SelectItem value="mit">MIT</SelectItem>
                      <SelectItem value="bsd">BSD</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <label className="text-sm text-gray-600 dark:text-gray-400 mb-2 block">
                    {t('artifacts.securityStatus')}
                  </label>
                  <Select>
                    <SelectTrigger>
                      <SelectValue placeholder={t('artifacts.anyStatus')} />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="secure">{t('artifacts.secure')}</SelectItem>
                      <SelectItem value="vulnerable">{t('artifacts.hasVulnerabilities')}</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <label className="text-sm text-gray-600 dark:text-gray-400 mb-2 block">
                    {t('artifacts.dateRange')}
                  </label>
                  <Select>
                    <SelectTrigger>
                      <SelectValue placeholder={t('artifacts.anyTime')} />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="24h">{t('artifacts.last24Hours')}</SelectItem>
                      <SelectItem value="7d">{t('artifacts.last7Days')}</SelectItem>
                      <SelectItem value="30d">{t('artifacts.last30Days')}</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <label className="text-sm text-gray-600 dark:text-gray-400 mb-2 block">
                    {t('artifacts.sortBy')}
                  </label>
                  <Select value={sortBy} onValueChange={(v) => setSortBy(v as any)}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="downloads">{t('artifacts.mostDownloaded')}</SelectItem>
                      <SelectItem value="modified">{t('artifacts.recentlyUpdated')}</SelectItem>
                      <SelectItem value="name">{t('artifacts.nameAZ')}</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </CardContent>
          </Card>
        )}
      </div>

      {/* Artifacts Display */}
      <ScrollArea className="flex-1">
        {viewMode === 'grid' ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 pb-4">
            {sortedArtifacts.map((artifact) => (
              <Card 
                key={artifact.id} 
                className="hover:shadow-lg transition-shadow cursor-pointer group"
                onClick={() => setSelectedArtifact(artifact)}
              >
                <CardHeader className="pb-3">
                  <div className="flex items-start justify-between gap-2 mb-2">
                    <div className="flex items-center gap-2">
                      {getFormatIcon(artifact.format)}
                      <Badge className={formatBadgeColor(artifact.format)}>
                        {artifact.format}
                      </Badge>
                    </div>
                    {artifact.isLatest && (
                      <Badge variant="outline" className="text-xs">
                        {t('artifacts.latest')}
                      </Badge>
                    )}
                  </div>
                  <CardTitle className="text-lg group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                    {artifact.name}
                  </CardTitle>
                  <div className="flex items-center gap-2 mt-1">
                    <Badge variant="secondary" className="text-xs font-mono">
                      v{artifact.version}
                    </Badge>
                    {getVulnerabilityBadge(artifact.vulnerability, t)}
                  </div>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-4 line-clamp-2 min-h-[2.5rem]">
                    {artifact.description}
                  </p>
                  
                  <div className="grid grid-cols-2 gap-3 text-sm mb-4">
                    <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                      <Download className="size-4" />
                      <span>{(artifact.downloads / 1000).toFixed(0)}K</span>
                    </div>
                    <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                      <Star className="size-4" />
                      <span>{artifact.stars}</span>
                    </div>
                    <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                      <HardDrive className="size-4" />
                      <span>{artifact.size}</span>
                    </div>
                    <div className="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                      <Clock className="size-4" />
                      <span>{new Date(artifact.modified).toLocaleDateString()}</span>
                    </div>
                  </div>

                  {artifact.tags && artifact.tags.length > 0 && (
                    <div className="flex flex-wrap gap-1">
                      {artifact.tags.slice(0, 3).map((tag, idx) => (
                        <Badge key={idx} variant="outline" className="text-xs">
                          {tag}
                        </Badge>
                      ))}
                    </div>
                  )}
                </CardContent>
              </Card>
            ))}
          </div>
        ) : (
          <div className="space-y-2 pb-4">
            {sortedArtifacts.map((artifact) => (
              <Card 
                key={artifact.id}
                className="hover:shadow-md transition-shadow cursor-pointer group"
                onClick={() => setSelectedArtifact(artifact)}
              >
                <CardContent className="p-4">
                  <div className="flex items-center gap-4">
                    <div className="flex-shrink-0">
                      {getFormatIcon(artifact.format)}
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 mb-1">
                        <h3 className="font-semibold text-gray-900 dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                          {artifact.name}
                        </h3>
                        <Badge variant="secondary" className="text-xs font-mono">
                          v{artifact.version}
                        </Badge>
                        <Badge className={formatBadgeColor(artifact.format)}>
                          {artifact.format}
                        </Badge>
                        {getVulnerabilityBadge(artifact.vulnerability, t)}
                        {artifact.license && (
                          <Badge variant="outline" className="text-xs">
                            {artifact.license}
                          </Badge>
                        )}
                      </div>
                      <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-1">
                        {artifact.description}
                      </p>
                    </div>
                    <div className="flex items-center gap-6 text-sm text-gray-600 dark:text-gray-400">
                      <div className="flex items-center gap-1">
                        <Download className="size-4" />
                        <span>{(artifact.downloads / 1000).toFixed(0)}K</span>
                      </div>
                      <div className="flex items-center gap-1">
                        <Star className="size-4" />
                        <span>{artifact.stars}</span>
                      </div>
                      <div className="flex items-center gap-1">
                        <HardDrive className="size-4" />
                        <span>{artifact.size}</span>
                      </div>
                      <div className="flex items-center gap-1">
                        <Clock className="size-4" />
                        <span>{new Date(artifact.modified).toLocaleDateString()}</span>
                      </div>
                    </div>
                    <Button variant="ghost" size="sm" className="flex-shrink-0">
                      <ChevronRight className="size-4" />
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </ScrollArea>
    </div>
  );
}

// Artifact Detail Page Component
function ArtifactDetailPage({ 
  artifact, 
  onBack,
  onCopy
}: { 
  artifact: ArtifactItem; 
  onBack: () => void;
  onCopy: (text: string, label: string) => void;
}) {
  const { t } = useLanguage();
  const [selectedVersion, setSelectedVersion] = useState(artifact.version);

  const getInstallCommand = () => {
    switch (artifact.format) {
      case 'Maven':
        return `<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>${artifact.name}</artifactId>
  <version>${selectedVersion}</version>
</dependency>`;
      case 'Docker':
        return `docker pull registry.example.com/${artifact.name}:${selectedVersion}`;
      case 'NPM':
        return `npm install ${artifact.name}@${selectedVersion}`;
      case 'PyPI':
        return `pip install ${artifact.name}==${selectedVersion}`;
      case 'NuGet':
        return `Install-Package ${artifact.name} -Version ${selectedVersion}`;
      default:
        return '';
    }
  };

  return (
    <div className="flex flex-col h-[calc(100vh-8rem)]">
      {/* Breadcrumb and Back Button */}
      <div className="mb-6">
        <Button 
          variant="ghost" 
          className="mb-4"
          onClick={onBack}
        >
          <ArrowLeft className="mr-2 size-4" />
          {t('common.back')}
        </Button>
        
        <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 mb-4">
          <span className="cursor-pointer hover:text-blue-600" onClick={onBack}>
            {t('artifacts.browserTitle')}
          </span>
          <ChevronRight className="size-4" />
          <span className="text-gray-900 dark:text-white font-medium">{artifact.name}</span>
        </div>

        {/* Header */}
        <div className="flex items-start justify-between gap-4">
          <div className="flex-1">
            <div className="flex items-center gap-3 mb-3">
              {getFormatIcon(artifact.format)}
              <div className="flex items-center gap-2 flex-wrap">
                <Badge className={formatBadgeColor(artifact.format)}>
                  {artifact.format}
                </Badge>
                {artifact.license && (
                  <Badge variant="outline">{artifact.license}</Badge>
                )}
                {getVulnerabilityBadge(artifact.vulnerability, t)}
                {artifact.isLatest && (
                  <Badge variant="secondary">{t('artifacts.latest')}</Badge>
                )}
              </div>
            </div>
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
              {artifact.name}
            </h1>
            <p className="text-gray-600 dark:text-gray-400 text-lg mb-4">
              {artifact.description}
            </p>
            <div className="flex items-center gap-6 text-sm text-gray-600 dark:text-gray-400">
              <div className="flex items-center gap-2">
                <Download className="size-4" />
                <span>{artifact.downloads.toLocaleString()} {t('artifacts.downloadsCount')}</span>
              </div>
              <div className="flex items-center gap-2">
                <Star className="size-4" />
                <span>{artifact.stars} {t('artifacts.starsCount')}</span>
              </div>
              <div className="flex items-center gap-2">
                <Calendar className="size-4" />
                <span>{t('artifacts.updated')} {new Date(artifact.modified).toLocaleDateString()}</span>
              </div>
              <div className="flex items-center gap-2">
                <HardDrive className="size-4" />
                <span>{artifact.size}</span>
              </div>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Select value={selectedVersion} onValueChange={setSelectedVersion}>
              <SelectTrigger className="w-48">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {artifact.versions?.map((version) => (
                  <SelectItem key={version} value={version}>
                    v{version} {version === artifact.version && `(${t('artifacts.latest')})`}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Button onClick={() => toast.success(t('artifacts.downloadStarted', { name: artifact.name }))}>
              <Download className="mr-2 size-4" />
              {t('common.download')}
            </Button>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="outline">
                  <MoreVertical className="size-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={() => toast.success(t('artifacts.addedToFavorites'))}>
                  <Star className="mr-2 size-4" />
                  {t('artifacts.addToFavorites')}
                </DropdownMenuItem>
                <DropdownMenuItem>
                  <Eye className="mr-2 size-4" />
                  {t('artifacts.watchChanges')}
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => onCopy(artifact.path, t('common.path'))}>
                  <Copy className="mr-2 size-4" />
                  {t('artifacts.copyPath')}
                </DropdownMenuItem>
                <DropdownMenuItem>
                  <ExternalLink className="mr-2 size-4" />
                  {t('artifacts.openInBrowser')}
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem className="text-red-600 dark:text-red-400">
                  <Trash2 className="mr-2 size-4" />
                  {t('common.delete')}
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </div>

      {/* Content */}
      <ScrollArea className="flex-1">
        <div className="space-y-6 pb-6">
          {/* Install Command */}
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle className="flex items-center gap-2">
                  <Terminal className="size-5" />
                  {t('artifacts.installation')}
                </CardTitle>
                <Button 
                  variant="outline" 
                  size="sm"
                  onClick={() => onCopy(getInstallCommand(), t('artifacts.installCommand'))}
                >
                  <Copy className="mr-2 size-4" />
                  {t('common.copy')}
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              <div className="bg-gray-900 dark:bg-gray-950 rounded-lg p-4">
                <pre className="text-sm text-gray-100 font-mono overflow-x-auto">
                  {getInstallCommand()}
                </pre>
              </div>
            </CardContent>
          </Card>

          {/* Tabs */}
          <Tabs defaultValue="readme">
            <TabsList className="w-full justify-start">
              <TabsTrigger value="readme">
                <BookOpen className="mr-2 size-4" />
                {t('artifacts.readme')}
              </TabsTrigger>
              <TabsTrigger value="dependencies">
                <GitBranch className="mr-2 size-4" />
                {t('artifacts.dependencies')}
              </TabsTrigger>
              <TabsTrigger value="versions">
                <History className="mr-2 size-4" />
                {t('artifacts.versions')}
              </TabsTrigger>
              <TabsTrigger value="security">
                <Shield className="mr-2 size-4" />
                {t('repositories.securityTab')}
              </TabsTrigger>
              <TabsTrigger value="stats">
                <BarChart3 className="mr-2 size-4" />
                {t('artifacts.statistics')}
              </TabsTrigger>
            </TabsList>

            <TabsContent value="readme" className="mt-6">
              <Card>
                <CardContent className="pt-6">
                  <div className="prose dark:prose-invert max-w-none">
                    <h3>{artifact.name}</h3>
                    <p>{artifact.description}</p>
                    <h4>{t('artifacts.features')}</h4>
                    <ul>
                      <li>{t('artifacts.feature1')}</li>
                      <li>{t('artifacts.feature2')}</li>
                      <li>{t('artifacts.feature3')}</li>
                      <li>{t('artifacts.feature4')}</li>
                    </ul>
                    <h4>{t('artifacts.quickStart')}</h4>
                    <p>{t('artifacts.quickStartDesc')}</p>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="dependencies" className="mt-6">
              <Card>
                <CardHeader>
                  <CardTitle>{t('artifacts.directDependencies')}</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    {['dependency-1', 'dependency-2', 'dependency-3'].map((dep, idx) => (
                      <div key={idx} className="flex items-center justify-between p-3 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors">
                        <div className="flex items-center gap-3">
                          <Package className="size-5 text-gray-400" />
                          <div>
                            <p className="font-medium text-gray-900 dark:text-white">{dep}</p>
                            <p className="text-sm text-gray-500 dark:text-gray-400">^1.0.0 • compile</p>
                          </div>
                        </div>
                        <Button variant="ghost" size="sm">
                          <ExternalLink className="size-4" />
                        </Button>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="versions" className="mt-6">
              <Card>
                <CardHeader>
                  <CardTitle>{t('artifacts.versionHistory')}</CardTitle>
                  <CardDescription>{t('artifacts.versionHistoryDesc')}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    {artifact.versions?.map((version, idx) => (
                      <div 
                        key={version}
                        className="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors"
                      >
                        <div className="flex items-center gap-4">
                          <Badge variant={version === artifact.version ? 'default' : 'outline'}>
                            v{version}
                          </Badge>
                          {version === artifact.version && (
                            <Badge variant="secondary">{t('artifacts.latest')}</Badge>
                          )}
                          <span className="text-sm text-gray-600 dark:text-gray-400">
                            {t('artifacts.releasedOn')} {t('common.jan')} {15 - idx}, 2024
                          </span>
                        </div>
                        <div className="flex items-center gap-2">
                          <Button variant="outline" size="sm" onClick={() => toast.success(t('artifacts.downloadStarted', { name: `${artifact.name} v${version}` }))}>
                            <Download className="size-4" />
                          </Button>
                          <Button variant="ghost" size="sm">
                            <FileText className="size-4" />
                          </Button>
                        </div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="security" className="mt-6">
              <Card>
                <CardHeader>
                  <CardTitle>{t('artifacts.securityReport')}</CardTitle>
                </CardHeader>
                <CardContent>
                  {artifact.vulnerability && 
                   (artifact.vulnerability.critical + artifact.vulnerability.high + 
                    artifact.vulnerability.medium + artifact.vulnerability.low) === 0 ? (
                    <div className="flex items-center gap-3 p-6 bg-green-50 dark:bg-green-950/20 border border-green-200 dark:border-green-900 rounded-lg">
                      <CheckCircle className="size-8 text-green-600 dark:text-green-400 flex-shrink-0" />
                      <div>
                        <p className="font-medium text-green-700 dark:text-green-300 text-lg">
                          {t('artifacts.noVulnerabilitiesFound')}
                        </p>
                        <p className="text-sm text-green-600 dark:text-green-400 mt-1">
                          {t('artifacts.noVulnerabilitiesDesc')}
                        </p>
                      </div>
                    </div>
                  ) : (
                    <div className="space-y-3">
                      <div className="p-4 bg-red-50 dark:bg-red-950/20 border border-red-200 dark:border-red-900 rounded-lg">
                        <div className="flex items-start gap-3">
                          <AlertTriangle className="size-6 text-red-500 flex-shrink-0 mt-1" />
                          <div className="flex-1">
                            <h4 className="font-medium text-red-900 dark:text-red-100 mb-1">
                              CVE-2024-1234: {t('artifacts.bufferOverflow')}
                            </h4>
                            <p className="text-sm text-red-700 dark:text-red-300 mb-3">
                              {t('artifacts.bufferOverflowDesc')}
                            </p>
                            <div className="flex items-center gap-2">
                              <Badge className="bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300">
                                {t('artifacts.highSeverity')}
                              </Badge>
                              <span className="text-sm text-red-600 dark:text-red-400">
                                CVSS {t('artifacts.score')}: 7.5
                              </span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  )}
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="stats" className="mt-6">
              <div className="grid grid-cols-3 gap-4 mb-6">
                <Card>
                  <CardContent className="pt-6">
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.totalDownloads')}</p>
                    <p className="text-3xl font-bold text-gray-900 dark:text-white">
                      {artifact.downloads.toLocaleString()}
                    </p>
                    <p className="text-sm text-green-600 dark:text-green-400 mt-1">
                      +12% {t('artifacts.thisWeek')}
                    </p>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="pt-6">
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.stars')}</p>
                    <p className="text-3xl font-bold text-gray-900 dark:text-white">
                      {artifact.stars}
                    </p>
                    <p className="text-sm text-green-600 dark:text-green-400 mt-1">
                      +5 {t('artifacts.thisMonth')}
                    </p>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="pt-6">
                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('artifacts.watchers')}</p>
                    <p className="text-3xl font-bold text-gray-900 dark:text-white">
                      {Math.floor(artifact.stars * 0.3)}
                    </p>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                      {t('artifacts.activeUsers')}
                    </p>
                  </CardContent>
                </Card>
              </div>
              <Card>
                <CardHeader>
                  <CardTitle>{t('artifacts.recentDownloads')}</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    {[
                      { user: 'john.doe@company.com', time: '2', version: selectedVersion },
                      { user: 'jane.smith@startup.io', time: '5', version: selectedVersion },
                      { user: 'bob.wilson@enterprise.com', time: '24', version: selectedVersion },
                    ].map((download, idx) => (
                      <div key={idx} className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
                        <div className="flex items-center gap-3">
                          <User className="size-4 text-gray-400" />
                          <span className="text-sm text-gray-900 dark:text-white">{download.user}</span>
                        </div>
                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                          <span>v{download.version}</span>
                          <div className="flex items-center gap-1">
                            <Clock className="size-4" />
                            {download.time} {t('artifacts.hoursAgo')}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </div>
      </ScrollArea>
    </div>
  );
}